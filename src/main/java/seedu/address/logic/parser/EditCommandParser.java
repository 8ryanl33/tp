package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    private static final List<Prefix> SUPPORTED_PREFIXES =
            List.of(PREFIX_STUDENT_ID, PREFIX_NAME, PREFIX_EMAIL, PREFIX_REMARK);
    private static final Pattern PARAMETER_PREFIX = Pattern.compile("(?:^|\\p{javaWhitespace})([A-Za-z]+/)");
    private static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter: %s.";
    private static final String MESSAGE_MISSING_STUDENT_ID = "Missing required parameter: i/.";
    private static final String MESSAGE_EMPTY_STUDENT_ID = "Parameter i/ cannot be empty.";

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.contains("\n") || trimmedArgs.contains("\r")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        rejectUnknownPrefixes(trimmedArgs);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(trimmedArgs, PREFIX_STUDENT_ID, PREFIX_NAME, PREFIX_EMAIL, PREFIX_REMARK);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STUDENT_ID, PREFIX_NAME, PREFIX_EMAIL, PREFIX_REMARK);
        StudentId studentId = parseStudentId(argMultimap);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_REMARK).isPresent()) {
            editPersonDescriptor.setRemark(ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).get()));
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(studentId, editPersonDescriptor);
    }

    private static StudentId parseStudentId(ArgumentMultimap arguments) throws ParseException {
        Optional<String> studentId = arguments.getValue(PREFIX_STUDENT_ID);
        if (studentId.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_STUDENT_ID);
        }
        if (studentId.get().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_STUDENT_ID);
        }
        return ParserUtil.parseStudentId(studentId.get());
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

}

