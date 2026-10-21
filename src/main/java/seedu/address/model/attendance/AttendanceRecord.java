package seedu.address.model.attendance;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

import seedu.address.model.label.Label;

/**
 * Records that a student attended a group on a particular date.
 * Both fields are immutable. Group identity follows the same case comparison as labels.
 */
public class AttendanceRecord {

    private final LocalDate date;
    private final Label group;

    /**
     * Creates an attendance record for the given date and group.
     */
    public AttendanceRecord(LocalDate date, Label group) {
        requireAllNonNull(date, group);
        this.date = date;
        this.group = group;
    }

    /**
     * Returns the date on which attendance was marked.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the group for which attendance was marked.
     */
    public Label getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttendanceRecord otherRecord)) {
            return false;
        }
        return date.equals(otherRecord.date) && normalizedGroup().equals(otherRecord.normalizedGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, normalizedGroup());
    }

    @Override
    public String toString() {
        return date + ": " + group.labelName;
    }

    private String normalizedGroup() {
        return group.labelName.toLowerCase(Locale.ROOT);
    }
}
