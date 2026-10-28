package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.model.attendance.AttendanceRecord;
import seedu.address.model.attendance.AttendanceReport;
import seedu.address.model.attendance.AttendanceReport.AttendanceEntry;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Checks the attendance view's content. Skipped where JavaFX cannot start, such as on a machine without a display;
 * CI runs it on Linux under a virtual display.
 */
public class AttendanceListPanelTest {

    private static final LocalDate DATE = LocalDate.parse("2026-10-20");
    private static final seedu.address.model.label.Label GROUP = new seedu.address.model.label.Label("Tutorial 1");

    private static boolean isToolkitAvailable;

    @BeforeAll
    public static void startToolkit() {
        try {
            Platform.startup(() -> { });
            isToolkitAvailable = true;
        } catch (IllegalStateException e) {
            isToolkitAvailable = true; // already started by another test
        } catch (RuntimeException | UnsatisfiedLinkError e) {
            isToolkitAvailable = false;
        }
    }

    @Test
    public void constructor_report_showsTitleTotalsAndMembers() throws Exception {
        assumeTrue(isToolkitAvailable);
        Person present = new PersonBuilder(ALICE).withLabels("Tutorial 1")
                .withAttendance(new AttendanceRecord(DATE, GROUP)).build();
        Person absent = new PersonBuilder(BENSON).withLabels("Tutorial 1").build();
        AttendanceReport report = AttendanceReport.of(List.of(present, absent), DATE, GROUP);

        Parent root = onFxThread(() -> layOut(new AttendanceListPanel(report).getRoot()));

        assertEquals("Attendance: Tutorial 1 on 2026-10-20", ((Label) root.lookup("#title")).getText());
        assertEquals("Present: 1    Absent: 1", ((Label) root.lookup("#totals")).getText());
        assertEquals(report.getEntries(), ((ListView<?>) root.lookup("#attendanceListView")).getItems());
        assertEquals(1, root.lookupAll(".attendance_present").size());
        assertEquals(1, root.lookupAll(".attendance_absent").size());
    }

    @Test
    public void constructor_emptyReport_showsZeroTotals() throws Exception {
        assumeTrue(isToolkitAvailable);
        AttendanceReport report = AttendanceReport.of(List.of(), DATE, GROUP);

        Parent root = onFxThread(() -> layOut(new AttendanceListPanel(report).getRoot()));

        assertEquals("Present: 0    Absent: 0", ((Label) root.lookup("#totals")).getText());
        assertTrue(((ListView<?>) root.lookup("#attendanceListView")).getItems().isEmpty());
        assertTrue(root.lookupAll("#status").isEmpty());
    }

    @Test
    public void attendanceCard_entry_showsStudentAndStatusBadge() throws Exception {
        assumeTrue(isToolkitAvailable);
        Node presentCard = onFxThread(() -> new AttendanceCard(new AttendanceEntry(ALICE, true), 1).getRoot());
        Node absentCard = onFxThread(() -> new AttendanceCard(new AttendanceEntry(BENSON, false), 2).getRoot());

        assertEquals("1. ", ((Label) presentCard.lookup("#id")).getText());
        assertEquals("Alice Pauline", ((Label) presentCard.lookup("#name")).getText());
        assertEquals("Student ID: A1234567B", ((Label) presentCard.lookup("#studentId")).getText());
        Label presentStatus = (Label) presentCard.lookup("#status");
        assertEquals("Present", presentStatus.getText());
        assertTrue(presentStatus.getStyleClass().contains("attendance_present"));
        assertFalse(presentStatus.getStyleClass().contains("attendance_absent"));

        Label absentStatus = (Label) absentCard.lookup("#status");
        assertEquals("Absent", absentStatus.getText());
        assertTrue(absentStatus.getStyleClass().contains("attendance_absent"));
    }

    @Test
    public void attendanceCard_longNames_wrapAndKeepStatusVisibleWhenResized() throws Exception {
        assumeTrue(isToolkitAvailable);
        onFxThread(() -> {
            Person present = new PersonBuilder(ALICE).withName("M".repeat(100)).withLabels("Tutorial 1")
                    .withAttendance(new AttendanceRecord(DATE, GROUP)).build();
            Person absent = new PersonBuilder(BENSON).withName("Long ".repeat(19) + "Name")
                    .withLabels("Tutorial 1").build();
            AttendanceReport report = AttendanceReport.of(List.of(present, absent), DATE, GROUP);
            Region root = new AttendanceListPanel(report).getRoot();
            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());

            for (double width : new double[] {400, 800, 340, 400}) {
                root.resize(width, 400);
                root.applyCss();
                for (int pass = 0; pass < 3; pass++) {
                    root.layout();
                }

                assertEquals(2, root.lookupAll("#cardPane").size());
                for (Node card : root.lookupAll("#cardPane")) {
                    Bounds cardBounds = card.localToScene(card.getLayoutBounds());
                    assertTrue(cardBounds.getMaxX() <= width + 1, "Attendance card exceeds the list width");
                    Label name = (Label) card.lookup("#name");
                    if (width <= 400) {
                        assertTrue(name.getHeight() > 2 * name.getFont().getSize(), "Long name should wrap");
                    }
                    Bounds nameBounds = name.localToScene(name.getLayoutBounds());
                    Label status = (Label) card.lookup("#status");
                    Bounds statusBounds = status.localToScene(status.getLayoutBounds());
                    assertTrue(nameBounds.getMaxX() <= statusBounds.getMinX(), "Name overlaps the status");

                    for (Node node : card.lookupAll(".label")) {
                        Label label = (Label) node;
                        Bounds bounds = label.localToScene(label.getLayoutBounds());
                        assertTrue(bounds.getMinX() >= cardBounds.getMinX(), "Label exceeds the card's left edge");
                        assertTrue(bounds.getMaxX() <= cardBounds.getMaxX() + 1,
                                "Label exceeds the card's right edge");
                        assertTrue(bounds.getMaxY() <= cardBounds.getMaxY() + 1,
                                "Label exceeds the card's bottom edge");
                        assertEquals(label.getText(), ((Text) label.lookup(".text")).getText(),
                                "Label text is truncated");
                    }
                }
                root.lookupAll(".scroll-bar").stream().map(ScrollBar.class::cast)
                        .filter(bar -> bar.getOrientation() == Orientation.HORIZONTAL)
                        .forEach(bar -> assertFalse(bar.isVisible(), "Attendance list needs horizontal scrolling"));
            }
            return null;
        });
    }

    /**
     * Places {@code root} in a scene and lays it out, so that its list creates cells for the visible rows.
     */
    private static Parent layOut(Parent root) {
        new Scene(root, 400, 400);
        root.applyCss();
        root.layout();
        return root;
    }

    private static <T> T onFxThread(Supplier<T> supplier) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(supplier.get());
            } catch (RuntimeException | AssertionError e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(10, TimeUnit.SECONDS);
    }
}
