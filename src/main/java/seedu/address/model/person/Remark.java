package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a person's optional remark. Accepts any non-null text, including empty text.
 */
public class Remark {
    public static final String MESSAGE_CONSTRAINTS = "Able to take any values and can be blank";

    public final String value;

    /**
     * Constructs a {@code Remark} from non-null text.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
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

        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
