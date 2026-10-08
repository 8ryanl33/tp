package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a student's email, preserving local-part case and normalising the domain to lowercase.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS =
            "Email must have the form name@example.com and meet the supported email format.";
    public static final int MAX_LENGTH = 254;

    // Reject boundary and consecutive periods while allowing the other supported symbols at either end.
    private static final String LOCAL_PART_REGEX =
            "(?!\\.)(?![^@]*\\.\\.)[A-Za-z0-9._+-]{1,64}(?<!\\.)";
    // Each non-final label has 1-63 characters and alphanumeric boundaries.
    private static final String DOMAIN_LABEL_REGEX = "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?";
    private static final String DOMAIN_REGEX = "(?:" + DOMAIN_LABEL_REGEX + "\\.)+[A-Za-z]{2,63}";
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}, converting only its domain to lowercase using {@link Locale#ROOT}.
     *
     * @param email A valid email address.
     * @throws NullPointerException if {@code email} is null.
     * @throws IllegalArgumentException if the email address is invalid.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        int domainStart = email.indexOf('@') + 1;
        value = email.substring(0, domainStart) + email.substring(domainStart).toLowerCase(Locale.ROOT);
    }

    /**
     * Returns whether an email meets the supported ASCII format and 254-character length limit.
     * The local part has 1-64 characters; the domain has at least two labels and a final label of 2-63 letters.
     * Surrounding whitespace is not accepted and mailbox existence is not checked.
     *
     * @param test The non-null email to validate.
     * @return Whether the email is valid.
     * @throws NullPointerException if {@code test} is null.
     */
    public static boolean isValidEmail(String test) {
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
