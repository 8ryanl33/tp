package seedu.address.logic.parser;

import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code UndoCommand}.
 */
public class UndoCommandParser implements Parser<UndoCommand> {

    /**
     * Parses arguments supplied after the undo command word.
     *
     * @param args arguments supplied after the command word.
     * @return an undo command.
     * @throws ParseException if the arguments do not conform to the command format.
     */
    @Override
    public UndoCommand parse(String args) throws ParseException {
        return new UndoCommand();
    }
}
