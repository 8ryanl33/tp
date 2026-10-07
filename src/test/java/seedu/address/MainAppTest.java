package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
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
        assertEquals(0, logic.getFilteredPersonList().size());
        logic.execute("add n/Samuel i/A0123456B e/sam@example.com");
        assertEquals(1, storage.readAddressBook().orElseThrow().getPersonList().size());
    }

    @Test
    public void startup_corruptData_blocksAddAndPreservesFile() throws Exception {
        Storage storage = createStorage();
        Files.writeString(storage.getAddressBookFilePath(), "corrupt data");
        Logic logic = new TestApp().loadLogic(storage);
        assertEquals(0, logic.getFilteredPersonList().size());
        assertThrows(CommandException.class, LogicManager.MESSAGE_DATA_UNAVAILABLE, ()
            -> logic.execute("add n/Samuel i/A0123456B e/sam@example.com"));
        logic.execute("list");
        assertEquals("corrupt data", Files.readString(storage.getAddressBookFilePath()));
    }

    private Storage createStorage() {
        return new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("students.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
    }

    private static class TestApp extends MainApp {
        Logic loadLogic(Storage storage) {
            Model initialModel = initModelManager(storage, new UserPrefs());
            return initLogic(initialModel, storage);
        }
    }
}
