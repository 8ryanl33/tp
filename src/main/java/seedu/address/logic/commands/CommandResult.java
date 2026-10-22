package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.attendance.AttendanceReport;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The attendance report to show in place of the student list, or null to show the student list. */
    private final AttendanceReport attendanceReport;

    /** Attendance records were changed, so an open attendance report should be refreshed. */
    private final boolean attendanceUpdated;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit,
            AttendanceReport attendanceReport, boolean attendanceUpdated) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.attendanceReport = attendanceReport;
        this.attendanceUpdated = attendanceUpdated;
    }

    /**
     * Returns a result that shows {@code attendanceReport} in place of the student list.
     */
    public static CommandResult showingAttendance(String feedbackToUser, AttendanceReport attendanceReport) {
        requireNonNull(attendanceReport);
        return new CommandResult(feedbackToUser, false, false, attendanceReport, false);
    }

    /**
     * Returns a result for a command that changed attendance records.
     */
    public static CommandResult updatingAttendance(String feedbackToUser) {
        return new CommandResult(feedbackToUser, false, false, null, true);
    }

    /**
     * Returns a copy of this result that shows {@code attendanceReport} in place of the student list.
     */
    public CommandResult withAttendanceReport(AttendanceReport attendanceReport) {
        requireNonNull(attendanceReport);
        return new CommandResult(feedbackToUser, showHelp, exit, attendanceReport, attendanceUpdated);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    public Optional<AttendanceReport> getAttendanceReport() {
        return Optional.ofNullable(attendanceReport);
    }

    public boolean isAttendanceUpdated() {
        return attendanceUpdated;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(attendanceReport, otherCommandResult.attendanceReport)
                && attendanceUpdated == otherCommandResult.attendanceUpdated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, attendanceReport, attendanceUpdated);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("attendanceReport", attendanceReport)
                .add("attendanceUpdated", attendanceUpdated)
                .toString();
    }

}
