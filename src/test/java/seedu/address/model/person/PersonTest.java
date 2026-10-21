package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDENT_ID_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.label.Label;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    private static final String VALID_STUDENT_ID_OTHER_BOB = "C0123456D";
    private static final String VALID_LABEL_TUTORIAL = "Discrete Math Tutorial";

    @Test
    public void getLabels_modifySet_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getLabels().clear());
    }

    @Test
    public void getAttendance_modifySet_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getAttendance().clear());
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same student ID, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB)
                .withEmail(VALID_EMAIL_BOB).withLabels(VALID_LABEL_TUTORIAL).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different student ID, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withStudentId(VALID_STUDENT_ID_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // student ID differs in case, all other attributes same -> returns true
        // Used to be `editedBob`, edited to express intent better
        Person anotherPersonBob = new PersonBuilder(BOB)
                .withStudentId(BOB.getStudentId().toString().toLowerCase())
                .build();
        assertTrue(BOB.isSamePerson(anotherPersonBob));

        // same name, different student ID -> returns false
        anotherPersonBob = new PersonBuilder(BOB).withStudentId(VALID_STUDENT_ID_OTHER_BOB).build();
        assertFalse(BOB.isSamePerson(anotherPersonBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different student ID -> returns false
        editedAlice = new PersonBuilder(ALICE).withStudentId(VALID_STUDENT_ID_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different labels -> returns false
        editedAlice = new PersonBuilder(ALICE).withLabels(VALID_LABEL_TUTORIAL).build();
        assertFalse(ALICE.equals(editedAlice));

        AttendanceRecord record = new AttendanceRecord(LocalDate.parse("2026-10-20"), new Label("Tutorial 1"));
        editedAlice = new PersonBuilder(ALICE).withAttendance(record).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void constructor_withoutRemark_defaultsToEmptyRemark() {
        Person person = new Person(ALICE.getName(), ALICE.getStudentId(), ALICE.getEmail());

        assertEquals(ALICE.getStudentId(), person.getStudentId());
        assertEquals(new Remark(""), person.getRemark());
        assertTrue(person.getAttendance().isEmpty());
    }

    @Test
    public void constructor_nullRemark_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> withRemark(ALICE, null));
    }

    @Test
    public void equals_differentRemark_returnsFalseButSamePerson() {
        Person remarkedAlice = withRemark(ALICE, new Remark("Needs help with recursion"));
        assertEquals(new Remark("Needs help with recursion"), remarkedAlice.getRemark());
        assertFalse(ALICE.equals(remarkedAlice));
        assertTrue(ALICE.isSamePerson(remarkedAlice));

        Person remarkedAliceCopy = withRemark(ALICE, new Remark("Needs help with recursion"));
        assertEquals(remarkedAlice, remarkedAliceCopy);
        assertEquals(remarkedAlice.hashCode(), remarkedAliceCopy.hashCode());
        assertTrue(remarkedAlice.toString().contains("remark=Needs help with recursion"));
    }

    private Person withRemark(Person person, Remark remark) {
        return new Person(person.getName(), person.getStudentId(), person.getEmail(), person.getLabels(), remark);
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", studentId=" + ALICE.getStudentId() + ", email=" + ALICE.getEmail()
                + ", labels=" + ALICE.getLabels()
                + ", remark=" + ALICE.getRemark()
                + ", attendance=" + ALICE.getAttendance() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
