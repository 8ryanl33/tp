package seedu.address.model.label;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LabelTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Label(null));
    }

    @Test
    public void constructor_invalidLabelName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Label(""));
    }

    @Test
    public void constructor_validLabelName_preservesDisplayText() {
        Label label = new Label("Discrete Math Tutorial");

        assertEquals("Discrete Math Tutorial", label.labelName);
    }

    @Test
    public void isValidLabelName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Label.isValidLabelName(null));
    }

    @Test
    public void isValidLabelName_invalidLabelName_returnsFalse() {
        assertFalse(Label.isValidLabelName(""));
        assertFalse(Label.isValidLabelName(" "));
        assertFalse(Label.isValidLabelName("a".repeat(61)));
        assertFalse(Label.isValidLabelName("Tutorial/1"));
        assertFalse(Label.isValidLabelName("Tutorial\n1"));
        assertFalse(Label.isValidLabelName("---"));
    }

    @Test
    public void isValidLabelName_validLabelName_returnsTrue() {
        assertTrue(Label.isValidLabelName("A"));
        assertTrue(Label.isValidLabelName("a".repeat(60)));
        assertTrue(Label.isValidLabelName("Discrete Math Tutorial"));
        assertTrue(Label.isValidLabelName("Tutorial-1"));
        assertTrue(Label.isValidLabelName("Tutorial_1"));
        assertTrue(Label.isValidLabelName("Today's Tutorial"));
        assertTrue(Label.isValidLabelName("Tutorial (Monday)"));
        assertTrue(Label.isValidLabelName("Tutorial 1.2"));
    }
}
