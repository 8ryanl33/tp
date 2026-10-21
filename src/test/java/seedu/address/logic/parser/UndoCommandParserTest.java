package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UndoCommand;

public class UndoCommandParserTest {

    private final UndoCommandParser parser = new UndoCommandParser();

    @Test
    public void parse_noArguments_returnsUndoCommand() throws Exception {
        assertEquals(new UndoCommand(), parser.parse(""));
        assertEquals(new UndoCommand(), parser.parse("   \t"));
    }

    @Test
    public void parse_argumentsPresent_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1", expectedMessage);
        assertParseFailure(parser, " n/Samuel", expectedMessage);
    }
}
