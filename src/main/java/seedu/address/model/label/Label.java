package seedu.address.model.label;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a group label assigned to a student.
 * Guarantees: immutable; name is valid as declared in {@link #isValidLabelName(String)}
 */
public class Label {

    public static final int MAX_NAME_LENGTH = 60;
    public static final String MESSAGE_CONSTRAINTS = "Label names should be 1 to 60 characters long, "
            + "contain at least one letter or digit, and only contain letters, digits, spaces, hyphens, "
            + "underscores, apostrophes, parentheses, and periods.";

    public final String labelName;

    /**
     * Constructs a {@code Label}.
     *
     * @param labelName A valid label name.
     */
    public Label(String labelName) {
        requireNonNull(labelName);
        checkArgument(isValidLabelName(labelName), MESSAGE_CONSTRAINTS);
        this.labelName = labelName;
    }

    /**
     * Returns true if a given string is a valid label name.
     */
    public static boolean isValidLabelName(String test) {
        requireNonNull(test);

        if (test.isBlank() || test.codePointCount(0, test.length()) > MAX_NAME_LENGTH) {
            return false;
        }

        boolean hasLetterOrDigit = false;
        for (int i = 0; i < test.length(); i += Character.charCount(test.codePointAt(i))) {
            int codePoint = test.codePointAt(i);
            if (!isAllowedCodePoint(codePoint)) {
                return false;
            }
            hasLetterOrDigit = hasLetterOrDigit || Character.isLetterOrDigit(codePoint);
        }
        return hasLetterOrDigit;
    }

    private static boolean isAllowedCodePoint(int codePoint) {
        return Character.isLetterOrDigit(codePoint)
                || isCombiningMark(codePoint)
                || codePoint == ' '
                || codePoint == '-'
                || codePoint == '_'
                || codePoint == '\''
                || codePoint == '('
                || codePoint == ')'
                || codePoint == '.';
    }

    private static boolean isCombiningMark(int codePoint) {
        int characterType = Character.getType(codePoint);
        return characterType == Character.NON_SPACING_MARK
                || characterType == Character.COMBINING_SPACING_MARK
                || characterType == Character.ENCLOSING_MARK;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Label otherLabel)) {
            return false;
        }

        return labelName.equals(otherLabel.labelName);
    }

    @Override
    public int hashCode() {
        return labelName.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + labelName + ']';
    }
}
