package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AppPathsTest {
    private static final String STUDENT_DATA = "Student records beside the JAR.";
    private static final String PREFERENCES = "Preferences beside the JAR.";
    private static final String LOG_MESSAGE = "Logging beside the JAR.";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void runningFromClasses_usesWorkingDirectory() {
        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        assertEquals(workingDirectory, AppPaths.getApplicationDirectory());
        assertEquals(workingDirectory.resolve("data/addressbook.json"), AppPaths.getStudentDataFilePath());
        assertEquals(workingDirectory.resolve("preferences.json"), AppPaths.getPreferencesFilePath());
    }

    @Test
    public void runningFromJar_inAnotherDirectory_keepsFilesBesideJar() throws Exception {
        assertFilesBesideJar("installation");
    }

    @Test
    public void runningFromJar_withSpacesUnicodeAndPercentSigns_keepsFilesBesideJar() throws Exception {
        assertFilesBesideJar("Teach Assist Ω 100% %h %t %g %u %%");
    }

    private void assertFilesBesideJar(String installationFolderName) throws Exception {
        Path installationFolder = Files.createDirectory(temporaryFolder.resolve(installationFolderName));
        Path jar = createProbeJar(installationFolder.resolve("Teach Assist.jar"));
        Path launchFolder = Files.createDirectory(temporaryFolder.resolve("launch-folder"));
        Files.createDirectories(launchFolder.resolve("data"));
        Files.writeString(launchFolder.resolve("data/addressbook.json"), "Unrelated student data.");
        Files.writeString(launchFolder.resolve("preferences.json"), "Unrelated preferences.");

        runProbe(jar, launchFolder);
        Path secondLaunchFolder = Files.createDirectory(temporaryFolder.resolve("second-launch-folder"));
        runProbe(jar, secondLaunchFolder);

        assertEquals(STUDENT_DATA, Files.readString(installationFolder.resolve("data/addressbook.json")));
        assertEquals(PREFERENCES, Files.readString(installationFolder.resolve("preferences.json")));
        String logs = Files.readString(installationFolder.resolve("addressbook.log.0"));
        assertTrue(logs.contains(LOG_MESSAGE), logs);
        assertEquals("Unrelated student data.", Files.readString(launchFolder.resolve("data/addressbook.json")));
        assertEquals("Unrelated preferences.", Files.readString(launchFolder.resolve("preferences.json")));
        assertFalse(Files.exists(launchFolder.resolve("addressbook.log.0")));
        try (var files = Files.list(secondLaunchFolder)) {
            assertEquals(0, files.count());
        }
    }

    private Path createProbeJar(Path jar) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, StartupProbe.class.getName());
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar), manifest)) {
            for (Class<?> type : new Class<?>[] {AppPaths.class, LogsCenter.class, StartupProbe.class}) {
                String entry = type.getName().replace('.', '/') + ".class";
                output.putNextEntry(new JarEntry(entry));
                try (InputStream input = type.getResourceAsStream("/" + entry)) {
                    input.transferTo(output);
                }
                output.closeEntry();
            }
        }
        return jar;
    }

    private void runProbe(Path jar, Path launchFolder) throws Exception {
        Path javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java");
        Path outputFile = temporaryFolder.resolve("probe-output.txt");
        Process process = new ProcessBuilder(javaExecutable.toString(), "-jar", jar.toString())
                .directory(launchFolder.toFile()).redirectErrorStream(true)
                .redirectOutput(outputFile.toFile()).start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "File-location probe timed out.");
            assertEquals(0, process.exitValue(), Files.readString(outputFile));
        } finally {
            process.destroyForcibly();
        }
    }

    /**
     * Exercises paths and logging from a packaged JAR without requiring a graphical environment.
     */
    public static class StartupProbe {
        /**
         * Creates application files, or checks the files created by a previous launch.
         *
         * @param args Unused command-line arguments.
         * @throws IOException if an application file cannot be accessed.
         */
        public static void main(String[] args) throws IOException {
            Path data = AppPaths.getStudentDataFilePath();
            Files.createDirectories(data.getParent());
            checkOrCreate(data, STUDENT_DATA);
            checkOrCreate(AppPaths.getPreferencesFilePath(), PREFERENCES);
            LogsCenter.getLogger("AppPathsProbe").info(LOG_MESSAGE);
        }

        private static void checkOrCreate(Path file, String expected) throws IOException {
            if (Files.exists(file)) {
                if (!expected.equals(Files.readString(file))) {
                    throw new AssertionError("Loaded application files from the wrong folder: " + file);
                }
            } else {
                Files.writeString(file, expected);
            }
        }
    }
}
