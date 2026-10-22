package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;

import java.time.LocalDate;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.attendance.AttendanceReport;
import seedu.address.model.label.Label;

/**
 * Shows which current members of a group were present on a specified date.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: attendance "
            + "d/YYYY-MM-DD l/GROUP";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows a group's attendance on a date.\n"
            + "Parameters: " + PREFIX_DATE + "YYYY-MM-DD " + PREFIX_LABEL + "GROUP\n"
            + "Example: attendance d/2026-10-20 l/Tutorial 1";
    public static final String MESSAGE_SUCCESS = "Attendance for %1$s on %2$s: %3$d present, %4$d absent.";

    private final LocalDate date;
    private final Label group;

    /**
     * Creates a command to show a group's attendance on a date.
     *
     * @param date The non-null attendance date.
     * @param group The non-null group label.
     */
    public AttendanceCommand(LocalDate date, Label group) {
        requireAllNonNull(date, group);
        this.date = date;
        this.group = group;
    }

    /**
     * {@inheritDoc}
     * Reports on every current group member, including students hidden by the displayed student list's filter.
     */
    @Override
    public CommandResult execute(Model model) {
        Objects.requireNonNull(model);
        AttendanceReport report = AttendanceReport.of(model.getAddressBook().getPersonList(), date, group);
        return CommandResult.showingAttendance(String.format(MESSAGE_SUCCESS, group.labelName, date,
                report.getPresentCount(), report.getAbsentCount()), report);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AttendanceCommand otherCommand)) {
            return false;
        }
        return date.equals(otherCommand.date) && group.equals(otherCommand.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, group);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("date", date)
                .add("group", group)
                .toString();
    }
}
