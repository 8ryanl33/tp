package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.StudentId;

/**
 * Tests Student ID-based deletion against complete and filtered student lists.
 */
public class DeleteCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteCommand(null));
    }

    @Test
    public void execute_existingIdUnfilteredList_success() {
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
    }

    @Test
    public void execute_visibleStudentFilteredList_preservesFilter() {
        NameContainsKeywordsPredicate filter = new NameContainsKeywordsPredicate(List.of("Alice"));
        model.updateFilteredPersonList(filter);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(filter);
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_hiddenStudentFilteredList_deletesFromCompleteRecords() {
        NameContainsKeywordsPredicate filter = new NameContainsKeywordsPredicate(List.of("Benson"));
        model.updateFilteredPersonList(filter);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(filter);
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertFalse(model.hasPerson(ALICE));
    }

    @Test
    public void execute_emptyDisplayedList_deletesExistingStudent() {
        model.updateFilteredPersonList(person -> false);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.updateFilteredPersonList(person -> false);
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(ALICE.getStudentId()), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
    }

    @Test
    public void execute_lowercaseId_matchesNormalisedStudentId() {
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.deletePerson(ALICE);
        assertCommandSuccess(new DeleteCommand(new StudentId("a1234567b")), model,
                "Deleted student A1234567B: Alice Pauline.", expected);
    }

    @Test
    public void execute_unknownId_preservesRecordsAndFilter() {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Benson")));
        assertCommandFailure(new DeleteCommand(new StudentId("Z9999999Z")), model,
                "Student with ID Z9999999Z is not in the records.");
    }

    @Test
    public void equals() {
        DeleteCommand deleteAlice = new DeleteCommand(ALICE.getStudentId());
        assertTrue(deleteAlice.equals(deleteAlice));
        assertTrue(deleteAlice.equals(new DeleteCommand(new StudentId("a1234567b"))));
        assertFalse(deleteAlice.equals(new DeleteCommand(BENSON.getStudentId())));
        assertFalse(deleteAlice.equals(null));
        assertFalse(deleteAlice.equals(1));
    }

    @Test
    public void toStringMethod() {
        DeleteCommand command = new DeleteCommand(ALICE.getStudentId());
        assertEquals(DeleteCommand.class.getCanonicalName() + "{targetStudentId=A1234567B}", command.toString());
    }
}
