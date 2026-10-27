package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Tests the command-level feedback contract for {@code UndoCommand}.
 */
public class UndoCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_noPreviousState_reportsNothingToUndo() {
        // EP: direct command execution has no access to LogicManager's session history.
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO, expectedModel);
    }

    @Test
    public void equalsAndHashCode_compareCommandType() {
        // EP: another undo command is equal; null and other command types are unequal.
        UndoCommand command = new UndoCommand();
        assertEquals(command, command);
        assertEquals(command, new UndoCommand());
        assertEquals(command.hashCode(), new UndoCommand().hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
    }

    @Test
    public void execute_nullModel_rejected() {
        // EP: null violates the command's required model contract.
        UndoCommand command = new UndoCommand();
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
