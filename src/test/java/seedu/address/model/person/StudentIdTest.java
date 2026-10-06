package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentId(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        String invalidStudentId = "";
        assertThrows(IllegalArgumentException.class, () -> new StudentId(invalidStudentId));
    }

    @Test
    public void constructor_lowerCaseStudentId_normalizesToUpperCase() {
        assertEquals("A0123456B", new StudentId("a0123456b").toString());
    }

    @Test
    public void isValidStudentId() {
        // null student ID
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        // invalid student IDs
        assertFalse(StudentId.isValidStudentId("")); // empty string
        assertFalse(StudentId.isValidStudentId(" ")); // spaces only
        assertFalse(StudentId.isValidStudentId("A0123456")); // less than 9 characters
        assertFalse(StudentId.isValidStudentId("A0123456BC")); // more than 9 characters
        assertFalse(StudentId.isValidStudentId("A012 456B")); // spaces within student ID
        assertFalse(StudentId.isValidStudentId("A012345-B")); // punctuation within student ID
        assertFalse(StudentId.isValidStudentId("A012345_B")); // underscores within student ID
        assertFalse(StudentId.isValidStudentId("A012345\u00c9")); // non-ASCII letter

        // valid student IDs
        assertTrue(StudentId.isValidStudentId("A0123456B")); // uppercase letters and digits
        assertTrue(StudentId.isValidStudentId("a0123456b")); // lowercase letters and digits
        assertTrue(StudentId.isValidStudentId("123456789")); // digits only
        assertTrue(StudentId.isValidStudentId("ABCDEFGHI")); // letters only
    }

    @Test
    public void equals() {
        StudentId studentId = new StudentId("A0123456B");

        // same normalized values -> returns true
        assertTrue(studentId.equals(new StudentId("a0123456b")));

        // same object -> returns true
        assertTrue(studentId.equals(studentId));

        // null -> returns false
        assertFalse(studentId.equals(null));

        // different types -> returns false
        assertFalse(studentId.equals(5.0f));

        // different values -> returns false
        assertFalse(studentId.equals(new StudentId("A0123456C")));
    }

    @Test
    public void hashCode_sameNormalizedValue_returnsSameHashCode() {
        assertEquals(new StudentId("A0123456B").hashCode(), new StudentId("a0123456b").hashCode());
    }
}
