package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.attendance.AttendanceReport;
import seedu.address.model.label.Label;

public class CommandResultTest {
    private final AttendanceReport report = AttendanceReport.of(List.of(), LocalDate.parse("2026-10-20"),
            new Label("Tutorial 1"));

    @Test
    public void constructors_validFeedbackAndFlags_preserveValues() {
        // EP: empty, blank, and nonempty feedback are valid; each boolean flag may independently be true/false.
        // BVA: feedback lengths zero and one. There is no specified maximum.
        for (String feedback : List.of("", " ", "a", "feedback")) {
            CommandResult defaults = new CommandResult(feedback);
            assertEquals(feedback, defaults.getFeedbackToUser());
            assertFalse(defaults.isShowHelp());
            assertFalse(defaults.isExit());
            assertEquals(Optional.empty(), defaults.getAttendanceReport());
            assertFalse(defaults.isAttendanceUpdated());
            assertEquals(new CommandResult(feedback, false, false), defaults);
        }
        for (boolean help : List.of(false, true)) {
            for (boolean exit : List.of(false, true)) {
                CommandResult result = new CommandResult("feedback", help, exit);
                assertEquals("feedback", result.getFeedbackToUser());
                assertEquals(help, result.isShowHelp());
                assertEquals(exit, result.isExit());
                assertEquals(Optional.empty(), result.getAttendanceReport());
                assertFalse(result.isAttendanceUpdated());
            }
        }
    }

    @Test
    public void nullInputs_areRejected() {
        // EP: feedback must be non-null in every creation path; report arguments must be non-null.
        assertThrows(NullPointerException.class, () -> new CommandResult(null));
        assertThrows(NullPointerException.class, () -> new CommandResult(null, true, false));
        assertThrows(NullPointerException.class, () -> CommandResult.showingAttendance(null, report));
        assertThrows(NullPointerException.class, () -> CommandResult.updatingAttendance(null));
        assertThrows(NullPointerException.class, () -> CommandResult.showingAttendance("feedback", null));
        CommandResult original = CommandResult.showingAttendance("feedback", report);
        assertThrows(NullPointerException.class, () -> original.withAttendanceReport(null));
        assertEquals(Optional.of(report), original.getAttendanceReport());
        assertEquals("feedback", original.getFeedbackToUser());
    }

    @Test
    public void attendanceFactories_setAllFields() {
        // EP: showing a report and changing attendance produce different result contracts.
        CommandResult shown = CommandResult.showingAttendance("shown", report);
        assertEquals("shown", shown.getFeedbackToUser());
        assertEquals(Optional.of(report), shown.getAttendanceReport());
        assertFalse(shown.isAttendanceUpdated());
        assertFalse(shown.isShowHelp());
        assertFalse(shown.isExit());

        CommandResult changed = CommandResult.updatingAttendance("changed");
        assertEquals("changed", changed.getFeedbackToUser());
        assertEquals(Optional.empty(), changed.getAttendanceReport());
        assertTrue(changed.isAttendanceUpdated());
        assertFalse(changed.isShowHelp());
        assertFalse(changed.isExit());
    }

    @Test
    public void withAttendanceReport_preservesOtherFieldsAndOriginal() {
        // EP: copying a result without a report, with an existing report, or with attendanceUpdated set.
        AttendanceReport replacement = AttendanceReport.of(List.of(), LocalDate.parse("2026-10-21"),
                new Label("Tutorial 2"));
        for (CommandResult original : List.of(new CommandResult("feedback", true, true),
                CommandResult.showingAttendance("feedback", report), CommandResult.updatingAttendance("feedback"))) {
            Optional<AttendanceReport> previousReport = original.getAttendanceReport();
            CommandResult copy = original.withAttendanceReport(replacement);
            assertEquals(Optional.of(replacement), copy.getAttendanceReport());
            assertEquals(original.getFeedbackToUser(), copy.getFeedbackToUser());
            assertEquals(original.isShowHelp(), copy.isShowHelp());
            assertEquals(original.isExit(), copy.isExit());
            assertEquals(original.isAttendanceUpdated(), copy.isAttendanceUpdated());
            assertEquals(previousReport, original.getAttendanceReport());
        }
    }

    @Test
    public void equalsAndHashCode_compareAllResultFields() {
        // EP: same reference, equal independent values, null, other type, and each differing field.
        CommandResult base = new CommandResult("feedback");
        assertEquals(base, base);
        assertFalse(base.equals(null));
        assertFalse(base.equals(0.5f));
        assertNotEquals(base, new CommandResult("different"));
        assertNotEquals(base, new CommandResult("feedback", true, false));
        assertNotEquals(base, new CommandResult("feedback", false, true));
        assertNotEquals(base, CommandResult.showingAttendance("feedback", report));
        assertNotEquals(base, CommandResult.updatingAttendance("feedback"));
        AttendanceReport otherReport = AttendanceReport.of(List.of(), LocalDate.parse("2026-10-21"),
                new Label("Tutorial 1"));
        assertNotEquals(CommandResult.showingAttendance("feedback", report),
                CommandResult.showingAttendance("feedback", otherReport));
        for (CommandResult result : List.of(base, new CommandResult("feedback", true, true),
                CommandResult.showingAttendance("feedback", report), CommandResult.updatingAttendance("feedback"))) {
            CommandResult copy = result.getAttendanceReport().isPresent()
                    ? CommandResult.showingAttendance("feedback", AttendanceReport.of(List.of(), report.getDate(),
                            report.getGroup()))
                    : result.isAttendanceUpdated() ? CommandResult.updatingAttendance("feedback")
                            : new CommandResult("feedback", result.isShowHelp(), result.isExit());
            assertEquals(result, copy);
            assertEquals(copy, result);
            // Unequal objects may share hash codes; only equal-object hash consistency is required.
            assertEquals(result.hashCode(), copy.hashCode());
        }
    }

    @Test
    public void toStringMethod() {
        // EP: diagnostic output includes every result field, including report presence/update status.
        for (CommandResult result : List.of(new CommandResult("feedback"),
                new CommandResult("feedback", true, true),
                CommandResult.updatingAttendance("changed").withAttendanceReport(report))) {
            String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                    + result.getFeedbackToUser() + ", showHelp=" + result.isShowHelp()
                    + ", exit=" + result.isExit() + ", attendanceReport="
                    + result.getAttendanceReport().orElse(null) + ", attendanceUpdated="
                    + result.isAttendanceUpdated() + "}";
            assertEquals(expected, result.toString());
        }
    }
}
