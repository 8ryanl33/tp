package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.attendance.AttendanceReport;
import seedu.address.model.label.Label;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // different attendance values -> returns false
        AttendanceReport report = AttendanceReport.of(List.of(), LocalDate.parse("2026-10-20"),
                new Label("Tutorial 1"));
        assertFalse(commandResult.equals(CommandResult.showingAttendance("feedback", report)));
        assertFalse(commandResult.equals(CommandResult.updatingAttendance("feedback")));
        assertTrue(CommandResult.showingAttendance("feedback", report)
                .equals(CommandResult.showingAttendance("feedback", report)));
    }

    @Test
    public void attendance_factoriesAndCopy_setAttendanceFields() {
        AttendanceReport report = AttendanceReport.of(List.of(), LocalDate.parse("2026-10-20"),
                new Label("Tutorial 1"));
        assertEquals(Optional.empty(), new CommandResult("feedback").getAttendanceReport());
        assertFalse(new CommandResult("feedback").isAttendanceUpdated());
        assertEquals(Optional.of(report), CommandResult.showingAttendance("feedback", report).getAttendanceReport());
        assertTrue(CommandResult.updatingAttendance("feedback").isAttendanceUpdated());

        CommandResult refreshed = CommandResult.updatingAttendance("feedback").withAttendanceReport(report);
        assertEquals(Optional.of(report), refreshed.getAttendanceReport());
        assertTrue(refreshed.isAttendanceUpdated());
        assertEquals("feedback", refreshed.getFeedbackToUser());
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", attendanceReport=null, attendanceUpdated=false}";
        assertEquals(expected, commandResult.toString());
    }
}
