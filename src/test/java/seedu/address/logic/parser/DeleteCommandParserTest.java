package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.StudentId;

public class DeleteCommandParserTest {
    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validIdAndWhitespace_returnsNormalisedDeleteCommand() {
        DeleteCommand expected = new DeleteCommand(new StudentId("A0123456B"));
        assertParseSuccess(parser, "i/A0123456B", expected);
        assertParseSuccess(parser, " \ti/  a0123456b  \t", expected);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_missingId_reportsMissingParameter() {
        assertParseFailure(parser, "", "Missing required parameter: i/.");
        assertParseFailure(parser, " \t ", "Missing required parameter: i/.");
    }

    @Test
    public void parse_emptyId_reportsEmptyParameter() {
        assertParseFailure(parser, " i/", "Parameter i/ cannot be empty.");
        assertParseFailure(parser, " i/ \t", "Parameter i/ cannot be empty.");
    }

    @Test
    public void parse_repeatedId_reportsRepeatedParameter() {
        assertParseFailure(parser, "i/A0123456B i/A0234567C", "Parameter i/ must be specified only once.");
        assertParseFailure(parser, "i/ i/A0123456B", "Parameter i/ must be specified only once.");
        assertParseFailure(parser, "i/invalid\ti/A0123456B", "Parameter i/ must be specified only once.");
    }

    @Test
    public void parse_unknownPrefix_reportsUnknownParameter() {
        assertParseFailure(parser, "x/value i/A0123456B", "Unknown parameter: x/.");
        assertParseFailure(parser, "i/A0123456B n/Samuel", "Unknown parameter: n/.");
        assertParseFailure(parser, "i/A0123456B\te/sam@example.com", "Unknown parameter: e/.");
        assertParseFailure(parser, "I/A0123456B", "Unknown parameter: I/.");
    }

    @Test
    public void parse_invalidId_preservesFieldValidationMessage() {
        for (String id : new String[] {"123", "A012345-B", "A012 345B", "A0123456BB", "Ａ0123456B"}) {
            assertParseFailure(parser, "i/" + id, StudentId.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidStructure_reportsUsage() {
        assertParseFailure(parser, "1", DeleteCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "rubbish i/A0123456B", DeleteCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/A0123456B\nextra", DeleteCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/A0123456B\rextra", DeleteCommand.MESSAGE_INVALID_FORMAT);
    }
}
