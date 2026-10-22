package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.attendance.AttendanceReport.AttendanceEntry;

/**
 * A UI component that displays one group member's attendance.
 */
public class AttendanceCard extends UiPart<Region> {

    private static final String FXML = "AttendanceListCard.fxml";
    private static final String PRESENT_STYLE = "attendance_present";
    private static final String ABSENT_STYLE = "attendance_absent";

    public final AttendanceEntry entry;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label studentId;
    @FXML
    private Label status;

    /**
     * Creates an {@code AttendanceCard} with the given {@code AttendanceEntry} and index to display.
     */
    public AttendanceCard(AttendanceEntry entry, int displayedIndex) {
        super(FXML);
        this.entry = entry;
        id.setText(displayedIndex + ". ");
        name.setText(entry.student().getName().fullName);
        studentId.setText("Student ID: " + entry.student().getStudentId());
        status.setText(entry.isPresent() ? "Present" : "Absent");
        status.getStyleClass().add(entry.isPresent() ? PRESENT_STYLE : ABSENT_STYLE);
    }
}
