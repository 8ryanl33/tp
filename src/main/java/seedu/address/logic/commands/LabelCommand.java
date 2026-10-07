package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.label.Label;
import seedu.address.model.person.StudentId;

/**
 * Adds a group label to an existing student.
 */
public class LabelCommand extends Command {

    public static final String COMMAND_WORD = "label";

    public static final String MESSAGE_USAGE = "Usage: " + COMMAND_WORD + " "
            + PREFIX_LABEL + "LABEL_NAME "
            + PREFIX_STUDENT_ID + "STUDENT_ID";

    private final Label label;
    private final StudentId studentId;

    /**
     * Creates a LabelCommand to add {@code label} to the student with {@code studentId}.
     */
    public LabelCommand(Label label, StudentId studentId) {
        requireNonNull(label);
        requireNonNull(studentId);
        this.label = label;
        this.studentId = studentId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException("Label command execution is not implemented.");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LabelCommand otherLabelCommand)) {
            return false;
        }

        return label.equals(otherLabelCommand.label)
                && studentId.equals(otherLabelCommand.studentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("label", label)
                .add("studentId", studentId)
                .toString();
    }
}
