package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentIdContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL);

        // Throw an exception when a user does not put exactly one of either "n/", "i/" or "e/"
        if (!isExactlyOnePrefixPresent(argMultimap, PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL);

        Predicate<Person> predicate;
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            predicate = new NameContainsKeywordsPredicate(parseKeywords(argMultimap, PREFIX_NAME));
        } else if (argMultimap.getValue(PREFIX_STUDENT_ID).isPresent()) {
            predicate = new StudentIdContainsKeywordsPredicate(parseKeywords(argMultimap, PREFIX_STUDENT_ID));
        } else {
            predicate = new EmailContainsKeywordsPredicate(parseKeywords(argMultimap, PREFIX_EMAIL));
        }

        return new FindCommand(predicate);
    }

    /**
     * Parses the keywords belonging to {@code prefix}.
     */
    private static List<String> parseKeywords(ArgumentMultimap argumentMultimap, Prefix prefix) throws ParseException {
        String keywords = argumentMultimap.getValue(prefix).get();
        if (keywords.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return List.of(keywords.split("\\s+"));
    }

    /**
     * Returns true if exactly one of the given prefixes appears in the {@code ArgumentMultimap}.
     */
    private static boolean isExactlyOnePrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes)
                .filter(prefix -> argumentMultimap.getValue(prefix).isPresent())
                .count() == 1;
    }
}
