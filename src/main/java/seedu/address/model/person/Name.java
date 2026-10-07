package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's name, with surrounding whitespace removed and repeated spaces collapsed.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Name must contain 1-100 characters, include a letter, and use only letters, spaces, "
                    + "apostrophes, hyphens, or periods.";

    public static final int MAX_LENGTH = 100;
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{M} '’.-]+";

    public final String fullName;

    /**
     * Constructs a normalised {@code Name}, preserving capitalisation.
     *
     * @param name A name that is valid after normalisation.
     * @throws NullPointerException if {@code name} is null.
     * @throws IllegalArgumentException if the normalised name is invalid.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalise(name);
    }

    /**
     * Returns whether a name has 1-100 Unicode code points, an allowed character set, and at least one letter.
     * Length is checked after removing surrounding whitespace and collapsing repeated spaces.
     *
     * @param test The non-null name to validate.
     * @return Whether the normalised name is valid.
     * @throws NullPointerException if {@code test} is null.
     */
    public static boolean isValidName(String test) {
        String normalisedName = normalise(test);
        return normalisedName.codePointCount(0, normalisedName.length()) <= MAX_LENGTH
                && normalisedName.matches(VALIDATION_REGEX)
                && normalisedName.codePoints().anyMatch(Character::isLetter);
    }

    private static String normalise(String name) {
        return name.trim().replaceAll(" +", " ");
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
