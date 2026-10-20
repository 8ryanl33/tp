package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class MainAppTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void startup_missingData_startsEmptyAndAllowsAdd() throws Exception {
        Storage storage = createStorage();
        Logic logic = new TestApp().loadLogic(storage);
        assertEquals(MainApp.MESSAGE_NO_EXISTING_DATA, logic.getStartupMessage());
        assertEquals(0, logic.getFilteredPersonList().size());
        logic.execute("add n/Samuel i/A0123456B e/sam@example.com");
        assertEquals(1, storage.readAddressBook().orElseThrow().getPersonList().size());
    }

    @Test
    public void startup_corruptData_blocksAddAndPreservesFile() throws Exception {
        Storage storage = createStorage();
        Files.writeString(storage.getAddressBookFilePath(), "corrupt data");
        Logic logic = new TestApp().loadLogic(storage);
        assertEquals(JsonAddressBookStorage.MESSAGE_INVALID_FILE, logic.getStartupMessage());
        assertEquals(0, logic.getFilteredPersonList().size());
        assertThrows(CommandException.class, LogicManager.MESSAGE_DATA_UNAVAILABLE, ()
            -> logic.execute("add n/Samuel i/A0123456B e/sam@example.com"));
        logic.execute("list");
        assertEquals("corrupt data", Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void startup_nullStudentData_blocksModificationsAndPreservesFile() throws Exception {
        assertInvalidStudentDataPreserved("null");
    }

    @Test
    public void startup_nullStudentEntry_blocksModificationsAndPreservesFile() throws Exception {
        assertInvalidStudentDataPreserved("{\"persons\":[null]}");
    }

    private void assertInvalidStudentDataPreserved(String contents) throws Exception {
        Storage storage = createStorage();
        Files.writeString(storage.getAddressBookFilePath(), contents);
        Logic logic = new TestApp().loadLogic(storage);

        assertEquals(JsonAddressBookStorage.MESSAGE_INVALID_FILE, logic.getStartupMessage());
        assertEquals(0, logic.getFilteredPersonList().size());
        for (String command : List.of("add n/Samuel i/A0123456B e/sam@example.com", "clear",
                "delete i/A0123456B", "edit i/A0123456B n/Samuel")) {
            assertThrows(CommandException.class, LogicManager.MESSAGE_DATA_UNAVAILABLE, ()
                -> logic.execute(command));
        }
        logic.execute("list");
        assertEquals(contents, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void startup_nullPreferences_recoversDefaultsAndPreservesStudents() throws Exception {
        assertPreferencesRecovered("null");
    }

    @Test
    public void startup_nullGuiSettings_recoversDefaultsAndPreservesStudents() throws Exception {
        assertPreferencesRecovered("{\"guiSettings\":null}");
    }

    private void assertPreferencesRecovered(String contents) throws Exception {
        Storage storage = createStorage();
        storage.saveAddressBook(getTypicalAddressBook());
        String originalStudentData = Files.readString(storage.getAddressBookFilePath());
        JsonUserPrefsStorage prefsStorage = new JsonUserPrefsStorage(storage.getUserPrefsFilePath());
        Files.writeString(prefsStorage.getUserPrefsFilePath(), contents);
        TestApp app = new TestApp();

        UserPrefs recoveredPrefs = app.initPrefs(prefsStorage);
        Logic logic = app.loadLogic(storage, recoveredPrefs);

        assertEquals(new UserPrefs(), recoveredPrefs);
        assertEquals(new GuiSettings(), logic.getGuiSettings());
        assertEquals(new UserPrefs(), prefsStorage.readUserPrefs().orElseThrow());
        assertEquals(getTypicalAddressBook().getPersonList(), logic.getFilteredPersonList());
        assertEquals(originalStudentData, Files.readString(storage.getAddressBookFilePath()));
        logic.execute("add n/Samuel i/A0123456B e/sam@example.com");
        assertEquals(getTypicalAddressBook().getPersonList().size() + 1,
                storage.readAddressBook().orElseThrow().getPersonList().size());
    }

    @Test
    public void startup_validData_reportsNumberOfStudentsLoaded() throws Exception {
        Storage storage = createStorage();
        storage.saveAddressBook(getTypicalAddressBook());
        Logic logic = new TestApp().loadLogic(storage);
        int studentCount = getTypicalAddressBook().getPersonList().size();
        assertEquals(String.format(MainApp.MESSAGE_STUDENTS_LOADED, studentCount), logic.getStartupMessage());
        assertEquals(studentCount, logic.getFilteredPersonList().size());
    }

    @Test
    public void startup_oneStudent_reportsSingularStudentLoaded() throws Exception {
        Storage storage = createStorage();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ALICE);
        storage.saveAddressBook(addressBook);
        Logic logic = new TestApp().loadLogic(storage);
        assertEquals("1 student loaded.", logic.getStartupMessage());
    }

    @Test
    public void startup_noStudents_reportsZeroStudentsLoaded() throws Exception {
        Storage storage = createStorage();
        storage.saveAddressBook(new AddressBook());
        Logic logic = new TestApp().loadLogic(storage);
        assertEquals("0 students loaded.", logic.getStartupMessage());
    }

    @Test
    public void startup_duplicateStudentId_reportsDuplicateStudentId() throws Exception {
        Storage storage = createStorage();
        Files.copy(Paths.get("src", "test", "data", "JsonSerializableAddressBookTest",
                "duplicatePersonAddressBook.json"), storage.getAddressBookFilePath());
        Logic logic = new TestApp().loadLogic(storage);
        assertEquals("Unable to load student data: duplicate Student ID A1234567B detected.",
                logic.getStartupMessage());
    }

    private Storage createStorage() {
        return new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("students.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
    }

    private static class TestApp extends MainApp {
        Logic loadLogic(Storage storage) {
            return loadLogic(storage, new UserPrefs());
        }

        Logic loadLogic(Storage storage, UserPrefs prefs) {
            Model initialModel = initModelManager(storage, prefs);
            return initLogic(initialModel, storage);
        }
    }
}
