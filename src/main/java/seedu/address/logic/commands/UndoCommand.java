package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
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
    public CommandResult execute(Model model) throws CommandException {
        throw new UnsupportedOperationException("Undo has not been implemented.");
    }
}
