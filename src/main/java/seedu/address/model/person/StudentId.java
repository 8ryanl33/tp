package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's student ID in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidStudentId(String)}
 */
public class StudentId {

    public static final String MESSAGE_CONSTRAINTS =
            "Student ID must contain exactly 9 letters or digits, with no spaces.";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9]{9}";

    public final String value;

    /**
     * Constructs a {@code StudentId}.
     *
     * @param studentId A valid student ID.
     */
    public StudentId(String studentId) {
        requireNonNull(studentId);
        checkArgument(isValidStudentId(studentId), MESSAGE_CONSTRAINTS);
        value = studentId.toUpperCase(Locale.ROOT);
    }

    /**
     * Returns true if a given string is a valid student ID.
     */
    public static boolean isValidStudentId(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentId otherStudentId)) {
            return false;
        }

        return value.equals(otherStudentId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
