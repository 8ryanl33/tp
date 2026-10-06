package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_anyText_preservesValue() {
        for (String text : new String[] {"", " ", "Needs help with recursion", "Follow up: 你好!\nNext week"}) {
            assertEquals(text, new Remark(text).value);
            assertEquals(text, new Remark(text).toString());
        }
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Needs help");
        Remark copy = new Remark("Needs help");
        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(copy));
        assertEquals(remark.hashCode(), copy.hashCode());
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("Needs help"));
        assertFalse(remark.equals(new Remark("Different remark")));
    }
}
