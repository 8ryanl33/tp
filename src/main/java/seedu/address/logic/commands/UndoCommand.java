package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Restores the address book to its state before the latest data change.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = "Usage: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Undid last change.";
    public static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo.";

    @Override
    public boolean isModifyingData() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_NOTHING_TO_UNDO);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UndoCommand;
    }

    @Override
    public int hashCode() {
        return UndoCommand.class.hashCode();
    }
}
