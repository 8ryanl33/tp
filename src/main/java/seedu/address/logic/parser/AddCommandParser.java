package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.model.tag.Tag;

/**
 * Parses and validates add-student parameters before creating an {@code AddCommand}.
 */
public class AddCommandParser implements Parser<AddCommand> {

    private static final List<Prefix> REQUIRED_PREFIXES = List.of(PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL);
    private static final List<Prefix> UNIQUE_PREFIXES =
            List.of(PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL, PREFIX_REMARK);
    private static final Set<Prefix> SUPPORTED_PREFIXES =
            Set.of(PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL, PREFIX_TAG, PREFIX_REMARK);
    // Alphabetic prefix tokens are reserved; numeric text such as 8/10 remains an ordinary value.
    private static final Pattern PARAMETER_PREFIX = Pattern.compile("(?:^|\\p{javaWhitespace})([A-Za-z]+/)");
    private static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter: %s.";
    private static final String MESSAGE_REPEATED_PARAMETER = "Parameter %s must be specified only once.";
    private static final String MESSAGE_MISSING_PARAMETER = "Missing required parameter: %s.";
    private static final String MESSAGE_EMPTY_PARAMETER = "Parameter %s cannot be empty.";

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @param args The non-null command arguments.
     * @return An add command containing the validated student information.
     * @throws ParseException if the structure or any parameter value is invalid.
     * @throws NullPointerException if {@code args} is null.
     */
    public AddCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(trimmedArgs, PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL,
                        PREFIX_TAG, PREFIX_REMARK);

        validateStructure(trimmedArgs, argMultimap);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        StudentId studentId = ParserUtil.parseStudentId(argMultimap.getValue(PREFIX_STUDENT_ID).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).orElse(""));

        Person person = new Person(name, studentId, email, tagList, remark);

        return new AddCommand(person);
    }

    private static void validateStructure(String args, ArgumentMultimap arguments) throws ParseException {
        if (args.contains("\n") || args.contains("\r")) {
            throw new ParseException(AddCommand.MESSAGE_INVALID_FORMAT);
        }
        rejectUnknownPrefixes(args);
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(AddCommand.MESSAGE_INVALID_FORMAT);
        }
        rejectRepeatedPrefixes(arguments);
        requireMandatoryValues(arguments);
    }

    private static void rejectUnknownPrefixes(String args) throws ParseException {
        Matcher matcher = PARAMETER_PREFIX.matcher(args);
        while (matcher.find()) {
            Prefix prefix = new Prefix(matcher.group(1));
            if (!SUPPORTED_PREFIXES.contains(prefix)) {
                throw new ParseException(String.format(MESSAGE_UNKNOWN_PARAMETER, prefix));
            }
        }
    }

    private static void rejectRepeatedPrefixes(ArgumentMultimap arguments) throws ParseException {
        for (Prefix prefix : UNIQUE_PREFIXES) {
            if (arguments.getAllValues(prefix).size() > 1) {
                throw new ParseException(String.format(MESSAGE_REPEATED_PARAMETER, prefix));
            }
        }
    }

    private static void requireMandatoryValues(ArgumentMultimap arguments) throws ParseException {
        for (Prefix prefix : REQUIRED_PREFIXES) {
            if (arguments.getValue(prefix).isEmpty()) {
                throw new ParseException(String.format(MESSAGE_MISSING_PARAMETER, prefix));
            }
        }
        for (Prefix prefix : REQUIRED_PREFIXES) {
            if (arguments.getValue(prefix).orElseThrow().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_EMPTY_PARAMETER, prefix));
            }
        }
    }

}
