package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.label.Label;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.PersonBuilder;

public class LabelCommandTest {

    private static final String VALID_LABEL = "Discrete Math Tutorial";
    private static final String VALID_STUDENT_ID = "A0101010A";

    private final Person samuel = new PersonBuilder().withName("Samuel Tan")
            .withStudentId(VALID_STUDENT_ID)
            .withEmail("samuel@example.com")
            .build();
    private final Model model = new ModelManager(getAddressBookWith(samuel), new UserPrefs());

    @Test
    public void constructor_nullLabel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LabelCommand(null, new StudentId(VALID_STUDENT_ID)));
    }

    @Test
    public void constructor_nullStudentId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LabelCommand(new Label(VALID_LABEL), null));
    }

    @Test
    public void execute_existingStudent_addSuccessful() {
        LabelCommand labelCommand = new LabelCommand(new Label(VALID_LABEL), new StudentId(VALID_STUDENT_ID));
        Person labelledPerson = new PersonBuilder(samuel).withLabels(VALID_LABEL).build();
        Model expectedModel = new ModelManager(getAddressBookWith(samuel), new UserPrefs());
        expectedModel.setPerson(samuel, labelledPerson);

        String expectedMessage = "Added label \"Discrete Math Tutorial\" to Samuel Tan (A0101010A).";
        assertCommandSuccess(labelCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nonExistentStudent_throwsCommandException() {
        LabelCommand labelCommand = new LabelCommand(new Label(VALID_LABEL), new StudentId("B0101010B"));

        assertCommandFailure(labelCommand, model,
                "Student with ID B0101010B is not in the records. Consider using add to include the student.");
    }

    @Test
    public void execute_duplicateLabel_throwsCommandException() {
        Person labelledPerson = new PersonBuilder(samuel).withLabels(VALID_LABEL).build();
        Model modelWithLabelledPerson = new ModelManager(getAddressBookWith(labelledPerson), new UserPrefs());
        LabelCommand labelCommand = new LabelCommand(new Label("discrete math tutorial"),
                new StudentId(VALID_STUDENT_ID));

        assertCommandFailure(labelCommand, modelWithLabelledPerson,
                "Student A0101010A already has label \"Discrete Math Tutorial\".");
    }

    @Test
    public void equals() {
        LabelCommand labelCommand = new LabelCommand(new Label(VALID_LABEL), new StudentId(VALID_STUDENT_ID));
        LabelCommand labelCommandCopy = new LabelCommand(new Label(VALID_LABEL), new StudentId(VALID_STUDENT_ID));
        LabelCommand differentLabelCommand = new LabelCommand(new Label("Tutorial 2"),
                new StudentId(VALID_STUDENT_ID));
        LabelCommand differentStudentIdCommand = new LabelCommand(new Label(VALID_LABEL),
                new StudentId("B0101010B"));

        assertTrue(labelCommand.equals(labelCommand));
        assertTrue(labelCommand.equals(labelCommandCopy));
        assertFalse(labelCommand.equals(differentLabelCommand));
        assertFalse(labelCommand.equals(differentStudentIdCommand));
        assertFalse(labelCommand.equals(1));
        assertFalse(labelCommand.equals(null));
    }

    private static AddressBook getAddressBookWith(Person person) {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(person);
        return addressBook;
    }
}
