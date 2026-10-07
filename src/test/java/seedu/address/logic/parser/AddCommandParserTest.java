package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STUDENT_ID_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDENT_ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDENT_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String REQUIRED_FIELDS_BOB = NAME_DESC_BOB + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB;

    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_normalisedNameAndEmail_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("Anne-Marie O’Neill")
                .withEmail("Quiz+Sam@example.com").withTags().build();
        assertParseSuccess(parser, " n/  Anne-Marie   O’Neill  " + STUDENT_ID_DESC_BOB
                + " e/  Quiz+Sam@EXAMPLE.COM  ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_invalidNameAndEmailUnderNewRules_failure() {
        assertParseFailure(parser, " n/John 2" + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + " e/bob@localhost",
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkLengthBoundary_validatesLimit() {
        Person expectedPerson = new PersonBuilder(BOB).withTags()
                .withRemark("x".repeat(Remark.MAX_LENGTH)).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH),
                new AddCommand(expectedPerson));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/" + "x".repeat(Remark.MAX_LENGTH + 1),
                Remark.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkPresent_success() {
        String remark = "Needs help with recursion: follow up next week!";
        Person expectedPerson = new PersonBuilder(BOB).withRemark(remark).build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/" + remark + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, " r/" + remark + REQUIRED_FIELDS_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_remarkEmptyOrMissing_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags().withRemark("").build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB, new AddCommand(expectedPerson));
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/", new AddCommand(expectedPerson));
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + " r/   ", new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedRemark_failure() {
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/First r/Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + " r/ r/Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + REQUIRED_FIELDS_BOB + TAG_DESC_FRIEND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, NAME_DESC_BOB + " " + PREFIX_STUDENT_ID
                + VALID_STUDENT_ID_BOB.toLowerCase() + EMAIL_DESC_BOB + TAG_DESC_FRIEND,
                new AddCommand(expectedPerson));
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser, REQUIRED_FIELDS_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, STUDENT_ID_DESC_AMY + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STUDENT_ID));
        assertParseFailure(parser, EMAIL_DESC_AMY + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_STUDENT_ID, PREFIX_EMAIL));
    }

    @Test
    public void parse_repeatedInvalidValue_failure() {
        assertParseFailure(parser, INVALID_NAME_DESC + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, INVALID_STUDENT_ID_DESC + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STUDENT_ID));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_STUDENT_ID_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STUDENT_ID));
        assertParseFailure(parser, INVALID_EMAIL_DESC + REQUIRED_FIELDS_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + STUDENT_ID_DESC_AMY + EMAIL_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, VALID_NAME_BOB + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, expectedMessage);
        assertParseFailure(parser, NAME_DESC_BOB + VALID_STUDENT_ID_BOB + EMAIL_DESC_BOB, expectedMessage);
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + VALID_EMAIL_BOB, expectedMessage);
        assertParseFailure(parser, VALID_NAME_BOB + VALID_STUDENT_ID_BOB + VALID_EMAIL_BOB, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, INVALID_NAME_DESC + STUDENT_ID_DESC_BOB + EMAIL_DESC_BOB, Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_STUDENT_ID_DESC + EMAIL_DESC_BOB,
                StudentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + STUDENT_ID_DESC_BOB + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, REQUIRED_FIELDS_BOB + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_NAME_DESC + STUDENT_ID_DESC_BOB + INVALID_EMAIL_DESC,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + REQUIRED_FIELDS_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
