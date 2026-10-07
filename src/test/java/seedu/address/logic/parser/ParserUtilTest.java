package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_STUDENT_ID = "A012345-B";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_STUDENT_ID = "A0123456B";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_REMARK = "Quiz 1: 8/10. Consultation on Monday at 2pm.";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_unicodeAndRepeatedSpaces_returnsNormalisedName() throws Exception {
        assertEquals("José O’Neill", ParserUtil.parseName("  José   O’Neill  ").fullName);
        String name = "A".repeat(Name.MAX_LENGTH - 2) + "   B";
        assertEquals("A".repeat(Name.MAX_LENGTH - 2) + " B", ParserUtil.parseName(name).fullName);
    }

    @Test
    public void parseName_digitsOrOversizedName_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName("John 2"));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseName("A".repeat(Name.MAX_LENGTH + 1)));
    }

    @Test
    public void parseStudentId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudentId((String) null));
    }

    @Test
    public void parseStudentId_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseStudentId(INVALID_STUDENT_ID));
    }

    @Test
    public void parseStudentId_validValueWithoutWhitespace_returnsStudentId() throws Exception {
        StudentId expectedStudentId = new StudentId(VALID_STUDENT_ID);
        assertEquals(expectedStudentId, ParserUtil.parseStudentId(VALID_STUDENT_ID));
    }

    @Test
    public void parseStudentId_validValueWithWhitespace_returnsTrimmedStudentId() throws Exception {
        String studentIdWithWhitespace = WHITESPACE + VALID_STUDENT_ID.toLowerCase() + WHITESPACE;
        StudentId expectedStudentId = new StudentId(VALID_STUDENT_ID);
        assertEquals(expectedStudentId, ParserUtil.parseStudentId(studentIdWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseEmail_uppercaseDomain_returnsNormalisedEmail() throws Exception {
        assertEquals("Rachel+Quiz@example.com", ParserUtil.parseEmail("  Rachel+Quiz@EXAMPLE.COM  ").value);
    }

    @Test
    public void parseEmail_invalidDomainOrOversizedLocalPart_throwsParseException() {
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseEmail("a@localhost"));
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseEmail("a".repeat(65) + "@example.com"));
    }

    @Test
    public void parseRemark_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRemark(null));
    }

    @Test
    public void parseRemark_empty_returnsEmptyRemark() throws Exception {
        assertEquals(new Remark(""), ParserUtil.parseRemark(""));
    }

    @Test
    public void parseRemark_whitespaceOnly_returnsEmptyRemark() throws Exception {
        assertEquals(new Remark(""), ParserUtil.parseRemark(WHITESPACE));
    }

    @Test
    public void parseRemark_validValueWithoutWhitespace_returnsRemark() throws Exception {
        assertEquals(new Remark(VALID_REMARK), ParserUtil.parseRemark(VALID_REMARK));
    }

    @Test
    public void parseRemark_validValueWithWhitespace_returnsTrimmedRemark() throws Exception {
        assertEquals(new Remark(VALID_REMARK), ParserUtil.parseRemark(WHITESPACE + VALID_REMARK + WHITESPACE));
    }

    @Test
    public void parseRemark_internalWhitespaceAndCase_preservesText() throws Exception {
        String remark = "Needs  HELP\twith recursion. Quiz: 8/10!";
        assertEquals(new Remark(remark), ParserUtil.parseRemark(WHITESPACE + remark + WHITESPACE));
    }

    @Test
    public void parseRemark_maximumLengthAfterTrimming_returnsRemark() throws Exception {
        String remark = "x".repeat(Remark.MAX_LENGTH);
        assertEquals(new Remark(remark), ParserUtil.parseRemark(WHITESPACE + remark + WHITESPACE));
    }

    @Test
    public void parseRemark_overMaximumLengthAfterTrimming_throwsParseException() {
        String remark = "x".repeat(Remark.MAX_LENGTH + 1);
        assertThrows(ParseException.class, Remark.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseRemark(WHITESPACE + remark + WHITESPACE));
    }

    @Test
    public void parseRemark_supplementaryUnicode_countsCodePoints() throws Exception {
        String remark = "\uD83D\uDE00".repeat(Remark.MAX_LENGTH);
        assertEquals(new Remark(remark), ParserUtil.parseRemark(WHITESPACE + remark + WHITESPACE));
        assertThrows(ParseException.class, Remark.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseRemark(remark + "x"));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
