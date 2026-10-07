package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // unsupported symbol
        assertFalse(Name.isValidName("peter*")); // unsupported symbol
        assertFalse(Name.isValidName("12345"));
        assertFalse(Name.isValidName("peter the 2nd"));
        assertFalse(Name.isValidName(" .-'’ ")); // no letter
        assertFalse(Name.isValidName("\u0301")); // combining mark without a letter
        assertFalse(Name.isValidName("John\tDoe"));
        assertFalse(Name.isValidName("John\nDoe"));
        assertFalse(Name.isValidName("John/Doe"));

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr."));
        assertTrue(Name.isValidName("Anne-Marie O'Neill"));
        assertTrue(Name.isValidName("D’Arcy"));
        assertTrue(Name.isValidName("José 王小明"));
        assertTrue(Name.isValidName("Jose\u0301"));
    }

    @Test
    public void constructor_surroundingWhitespaceAndRepeatedSpaces_normalisesName() {
        assertEquals("John Doe", new Name(" \tJohn   Doe\r\n ").fullName);
        assertEquals("jOhN Doe", new Name(" jOhN   Doe ").fullName);
        assertEquals(new Name("John Doe"), new Name(" John   Doe "));
        assertEquals(new Name("John Doe").hashCode(), new Name(" John   Doe ").hashCode());
    }

    @Test
    public void isValidName_lengthBoundary_checksNormalisedLength() {
        assertTrue(Name.isValidName("A"));
        assertTrue(Name.isValidName("A".repeat(Name.MAX_LENGTH)));
        assertFalse(Name.isValidName("A".repeat(Name.MAX_LENGTH + 1)));
        String paddedName = "  " + "A".repeat(Name.MAX_LENGTH - 2) + "   B  ";
        assertEquals("A".repeat(Name.MAX_LENGTH - 2) + " B", new Name(paddedName).fullName);
        assertThrows(IllegalArgumentException.class, Name.MESSAGE_CONSTRAINTS, ()
            -> new Name("A".repeat(Name.MAX_LENGTH + 1)));
    }

    @Test
    public void isValidName_supplementaryLetters_countsCodePoints() {
        String name = "\uD801\uDC00".repeat(Name.MAX_LENGTH);
        assertTrue(Name.isValidName(name));
        assertEquals(name, new Name(name).fullName);
        assertFalse(Name.isValidName(name + "A"));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
