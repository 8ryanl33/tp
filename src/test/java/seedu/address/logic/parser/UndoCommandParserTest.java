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
        // EP: empty and whitespace-only argument strings are accepted.
        // BVA: zero non-whitespace characters.
        assertEquals(new UndoCommand(), parser.parse(""));
        assertEquals(new UndoCommand(), parser.parse("   \t"));
    }

    @Test
    public void parse_argumentsPresent_throwsParseException() {
        // EP: positional and parameter-like arguments are rejected.
        // BVA: one non-whitespace character is already too many.
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1", expectedMessage);
        assertParseFailure(parser, " n/Samuel", expectedMessage);
    }
}
