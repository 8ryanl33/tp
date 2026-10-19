package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {
    private static final StudentId STUDENT_ID = new StudentId("A0123456B");
    private static final String TARGET = "i/" + STUDENT_ID;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_tabSeparatedFields_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Amy Bee").withEmail("amy@example.com").build();
        assertParseSuccess(parser, "i/a0123456b\tn/Amy Bee\te/amy@example.com",
                new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", "Missing required parameter: i/.");
        assertParseFailure(parser, "n/Amy Bee", "Missing required parameter: i/.");
        assertParseFailure(parser, TARGET, EditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "i/ n/Amy Bee", "Parameter i/ cannot be empty.");
    }

    @Test
    public void parse_invalidStructure_failure() {
        assertParseFailure(parser, "1 n/Amy Bee", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, TARGET + " n/Amy Bee i/B0123456C",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_STUDENT_ID));
        assertParseFailure(parser, "i/123 n/Amy Bee", StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "i/A0123456B n/Amy Bee\ne/a@example.com", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "edit " + TARGET + " n/Amy Bee", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidField_failure() {
        assertParseFailure(parser, TARGET + " n/Amy2", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " e/bad", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " n/Amy2 e/bad", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " n/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET + " e/", Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_multipleFields_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName("Amy Bee").withEmail("amy@example.com").withRemark("Needs follow-up").build();
        assertParseSuccess(parser, TARGET + " e/amy@example.com r/Needs follow-up n/Amy Bee",
                new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_emptyRemark_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark("").build();
        assertParseSuccess(parser, TARGET + " r/   ", new EditCommand(STUDENT_ID, descriptor));
    }

    @Test
    public void parse_repeatedField_failure() {
        assertParseFailure(parser, TARGET + " n/Amy Bee n/Bob Bee",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, TARGET + " r/first r/second",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_REMARK));
    }

    @Test
    public void parse_unknownPrefix_failure() {
        assertParseFailure(parser, TARGET + " r/Needs help x/value", "Unknown parameter: x/.");
        assertParseFailure(parser, TARGET + " t/friends", "Unknown parameter: t/.");
    }
}
