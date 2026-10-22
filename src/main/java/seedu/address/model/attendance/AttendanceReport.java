package seedu.address.model.attendance;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.label.Label;
import seedu.address.model.person.Person;

/**
 * Shows which current members of a group were marked present on a date.
 * Members without a matching attendance record are absent. Guarantees: immutable.
 */
public class AttendanceReport {

    private final LocalDate date;
    private final Label group;
    private final List<AttendanceEntry> entries;

    private AttendanceReport(LocalDate date, Label group, List<AttendanceEntry> entries) {
        this.date = date;
        this.group = group;
        this.entries = List.copyOf(entries);
    }

    /**
     * Creates a report of the students who currently have {@code group}, in the order given.
     * Group labels are matched case-insensitively, as when labels are stored.
     *
     * @param students The non-null students to report on, in display order.
     * @param date The non-null attendance date.
     * @param group The non-null group label.
     * @return The attendance of every current group member.
     */
    public static AttendanceReport of(List<Person> students, LocalDate date, Label group) {
        requireAllNonNull(students, date, group);
        String normalizedGroup = normalize(group);
        AttendanceRecord record = new AttendanceRecord(date, group);
        List<AttendanceEntry> entries = students.stream()
                .filter(student -> student.getLabels().stream()
                        .anyMatch(label -> normalize(label).equals(normalizedGroup)))
                .map(student -> new AttendanceEntry(student, student.getAttendance().contains(record)))
                .toList();
        return new AttendanceReport(date, group, entries);
    }

    private static String normalize(Label label) {
        return label.labelName.toLowerCase(Locale.ROOT);
    }

    public LocalDate getDate() {
        return date;
    }

    public Label getGroup() {
        return group;
    }

    /**
     * Returns an unmodifiable list of group members and their attendance, in display order.
     */
    public List<AttendanceEntry> getEntries() {
        return entries;
    }

    public long getPresentCount() {
        return entries.stream().filter(AttendanceEntry::isPresent).count();
    }

    public long getAbsentCount() {
        return entries.size() - getPresentCount();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttendanceReport otherReport)) {
            return false;
        }
        return date.equals(otherReport.date)
                && group.equals(otherReport.group)
                && entries.equals(otherReport.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, group, entries);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date)
                .add("group", group)
                .add("entries", entries)
                .toString();
    }

    /**
     * One group member and whether they were marked present.
     *
     * @param student The group member.
     * @param isPresent Whether the student has an attendance record for the report's date and group.
     */
    public record AttendanceEntry(Person student, boolean isPresent) {
        /**
         * Creates an entry for a non-null student.
         */
        public AttendanceEntry {
            Objects.requireNonNull(student);
        }
    }
}
