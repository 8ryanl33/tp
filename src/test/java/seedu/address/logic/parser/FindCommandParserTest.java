package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.label.Label;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.LabelMatchesPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.RemarkContainsKeywordsPredicate;
import seedu.address.model.person.StudentIdContainsKeywordsPredicate;

public class FindCommandParserTest {
    private static final String SEARCH_PARAMETER = "n/, i/, e/, r/, or l/";
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
    public void parse_wordsBeforeFirstPrefix_throwsParseException() {
        assertParseFailure(parser, "Alice n/Bob",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_wordsBeforeFirstPrefixWithOtherErrors_throwsParseExceptionForWordsBeforeFirstPrefix() {
        assertParseFailure(parser, "Alice p/123",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "Alice n/Bob n/Carol",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
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
    public void parse_unknownPrefixWithRepeatedPrefix_throwsParseExceptionForUnknownPrefix() {
        assertParseFailure(parser, " p/123 n/Alice n/Bob",
                String.format(MESSAGE_UNKNOWN_PARAMETER, new Prefix("p/")));
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


    @Test
    public void parse_validLabel_preservesCompleteNameAndWhitespace() {
        FindCommand expected = new FindCommand(new LabelMatchesPredicate(new Label("Tutorial  1")));
        assertParseSuccess(parser, "  l/  Tutorial  1   ", expected);
        assertParseSuccess(parser, "\tl/Tutorial  1\t", expected);
    }

    @Test
    public void parse_labelLengthBoundaries_success() {
        for (String label : List.of("A", "A".repeat(60))) {
            assertParseSuccess(parser, "l/" + label,
                    new FindCommand(new LabelMatchesPredicate(new Label(label))));
        }
    }

    @Test
    public void parse_emptyLabel_reportsEmptyParameter() {
        assertParseFailure(parser, "l/", "Parameter l/ cannot be empty.");
        assertParseFailure(parser, "l/   ", "Parameter l/ cannot be empty.");
    }

    @Test
    public void parse_repeatedLabel_reportsRepeatedParameter() {
        assertParseFailure(parser, "l/Tutorial 1 l/Tutorial 2", "Parameter l/ must be specified only once.");
        assertParseFailure(parser, "l/ l/Tutorial 2", "Parameter l/ must be specified only once.");
    }

    @Test
    public void parse_labelMixedWithAnotherSearchField_reportsMultipleFields() {
        for (String other : List.of("n/Alice", "i/A1234567B", "e/alice@example.com", "r/recursion")) {
            assertParseFailure(parser, "l/Tutorial 1 " + other,
                    String.format(MESSAGE_REPEATED_PARAMETER, SEARCH_PARAMETER));
            assertParseFailure(parser, other + " l/Tutorial 1",
                    String.format(MESSAGE_REPEATED_PARAMETER, SEARCH_PARAMETER));
        }
    }

    @Test
    public void parse_invalidLabel_reportsLabelConstraints() {
        for (String label : List.of("A".repeat(61), "---", "Tutorial/1", "Tutorial\n1", "Tutorial\t1",
                "Tutorial\u007f1", "Tutorial!")) {
            assertParseFailure(parser, "l/" + label, Label.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_labelWithUnsupportedPrefix_reportsUnknownParameter() {
        assertParseFailure(parser, "L/Tutorial 1", "Unknown parameter: L/.");
        assertParseFailure(parser, "l/Tutorial 1 p/123", "Unknown parameter: p/.");
        assertParseFailure(parser, "l/One l/Two p/123", "Unknown parameter: p/.");
    }

    @Test
    public void parse_wordsBeforeLabel_reportsInvalidFormatFirst() {
        assertParseFailure(parser, "extra l/Tutorial 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validRemarkArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new RemarkContainsKeywordsPredicate(List.of("recursion")));
        assertParseSuccess(parser, " r/recursion", expectedFindCommand);
        assertParseSuccess(parser, "r/recursion", expectedFindCommand);
    }

    @Test
    public void parse_multipleRemarkKeywords_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new RemarkContainsKeywordsPredicate(List.of("recursion", "loops")));
        assertParseSuccess(parser, " r/recursion loops", expectedFindCommand);
        assertParseSuccess(parser, " \n r/recursion \n \t loops  \t", expectedFindCommand);
    }

    @Test
    public void parse_numericSlashInRemarkKeyword_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new RemarkContainsKeywordsPredicate(List.of("8/10")));
        assertParseSuccess(parser, " r/8/10", expectedFindCommand);
    }

    @Test
    public void parse_emptyRemark_throwsParseException() {
        assertParseFailure(parser, " r/", String.format(MESSAGE_EMPTY_PARAMETER, CliSyntax.PREFIX_REMARK));
        assertParseFailure(parser, " r/ \n \t ",
                String.format(MESSAGE_EMPTY_PARAMETER, CliSyntax.PREFIX_REMARK));
    }

    @Test
    public void parse_duplicateRemarkPrefix_throwsParseException() {
        assertParseFailure(parser, " r/recursion r/loops",
                String.format(MESSAGE_REPEATED_PARAMETER, CliSyntax.PREFIX_REMARK));
    }

    @Test
    public void parse_remarkWithOtherSearchPrefix_throwsParseException() {
        for (String otherParameter : List.of("n/Alice", "i/A1234567B", "e/alice@example.com", "l/Tutorial 1")) {
            assertParseFailure(parser, " r/recursion " + otherParameter,
                    String.format(MESSAGE_REPEATED_PARAMETER, SEARCH_PARAMETER));
            assertParseFailure(parser, " " + otherParameter + " r/recursion",
                    String.format(MESSAGE_REPEATED_PARAMETER, SEARCH_PARAMETER));
        }
    }

    @Test
    public void parse_wordsBeforeRemarkPrefix_throwsParseException() {
        assertParseFailure(parser, "Alice r/recursion",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unknownPrefixWithRemark_throwsParseException() {
        assertParseFailure(parser, " r/recursion p/123",
                String.format(MESSAGE_UNKNOWN_PARAMETER, new Prefix("p/")));
        assertParseFailure(parser, " R/recursion",
                String.format(MESSAGE_UNKNOWN_PARAMETER, new Prefix("R/")));
    }
}
