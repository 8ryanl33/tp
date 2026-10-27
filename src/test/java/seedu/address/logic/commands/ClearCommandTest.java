package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        // EP: clearing an already empty address book succeeds.
        // BVA: zero stored students.
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        // EP: clearing several students removes every record.
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_hiddenSingleStudent_clearsCompleteAddressBook() {
        // EP: clearing operates on stored records even when the current filter shows nothing.
        // BVA: one stored student, right above the empty-data boundary.
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.updateFilteredPersonList(person -> false);
        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
    }


    @Test
    public void execute_nullModel_rejected() {
        // EP: null violates the command's required model contract.
        ClearCommand command = new ClearCommand();
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
