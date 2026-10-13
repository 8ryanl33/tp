package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.StudentIdContainsKeywordsPredicate;

public class FindCommandParserTest {
    private static final String SEARCH_PARAMETER = "n/, i/, or e/";
    private static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter: %s.";
    private static final String MESSAGE_REPEATED_PARAMETER = "Parameter %s must be specified only once.";
    private static final String MESSAGE_MISSING_PARAMETER = "Missing required parameter: %s.";
    private static final String MESSAGE_EMPTY_PARAMETER = "Parameter %s cannot be empty.";

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_MISSING_PARAMETER, SEARCH_PARAMETER));
    }

    @Test
    public void parse_noPrefix_throwsParseException() {
        assertParseFailure(parser, " Alice Bob", String.format(MESSAGE_MISSING_PARAMETER, SEARCH_PARAMETER));
    }

    @Test
    public void parse_multiplePrefixes_throwsParseException() {
        assertParseFailure(parser, " n/Alice i/A1234567B",
                String.format(MESSAGE_REPEATED_PARAMETER, SEARCH_PARAMETER));
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob",
                String.format(MESSAGE_REPEATED_PARAMETER, CliSyntax.PREFIX_NAME));
    }

    @Test
    public void parse_emptyPrefixValue_throwsParseException() {
        assertParseFailure(parser, " n/   ", String.format(MESSAGE_EMPTY_PARAMETER, CliSyntax.PREFIX_NAME));
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        assertParseFailure(parser, " p/123", String.format(MESSAGE_UNKNOWN_PARAMETER, new Prefix("p/")));
    }

    @Test
    public void parse_validNameArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, " n/Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n n/Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_validStudentIdArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new StudentIdContainsKeywordsPredicate(List.of("A1234567B")));
        assertParseSuccess(parser, " i/A1234567B", expectedFindCommand);
    }

    @Test
    public void parse_validEmailArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new EmailContainsKeywordsPredicate(List.of("alice@example.com")));
        assertParseSuccess(parser, " e/alice@example.com", expectedFindCommand);
    }

}
