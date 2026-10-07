package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_STUDENT_ID = "A012345-B";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_STUDENT_ID = BENSON.getStudentId().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_nameAndEmailNormalisation_returnsNormalisedPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson("  Anne-Marie   O’Neill  ", VALID_STUDENT_ID,
                "Quiz+Sam@EXAMPLE.COM", VALID_TAGS, "");
        Person restored = person.toModelType();
        assertEquals("Anne-Marie O’Neill", restored.getName().fullName);
        assertEquals("Quiz+Sam@example.com", restored.getEmail().value);
        String savedJson = JsonUtil.toJsonString(new JsonAdaptedPerson(restored));
        assertEquals(restored, JsonUtil.fromJsonString(savedJson, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void toModelType_digitNameOrSingleLabelDomain_throwsIllegalValueException() {
        JsonAdaptedPerson invalidName = new JsonAdaptedPerson("John 2", VALID_STUDENT_ID,
                VALID_EMAIL, VALID_TAGS, "");
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, invalidName::toModelType);
        JsonAdaptedPerson invalidEmail = new JsonAdaptedPerson(VALID_NAME, VALID_STUDENT_ID,
                "a@localhost", VALID_TAGS, "");
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, invalidEmail::toModelType);
    }

    @Test
    public void toModelType_oversizedRemark_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_STUDENT_ID,
                VALID_EMAIL, VALID_TAGS, "x".repeat(Remark.MAX_LENGTH + 1));
        assertThrows(IllegalValueException.class, Remark.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void jsonAdaptedPerson_maximumLengthRemark_roundTripPreservesText() throws Exception {
        Person original = new PersonBuilder(BENSON).withRemark("x".repeat(Remark.MAX_LENGTH)).build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(original));
        JsonAdaptedPerson restored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
        assertEquals(original, restored.toModelType());
    }

    @Test
    public void jsonAdaptedPerson_remark_roundTripPreservesText() throws Exception {
        for (String remark : new String[] {"", "Needs help with recursion", "Follow up: 你好!\nNext week"}) {
            Person original = new PersonBuilder(BENSON).withRemark(remark).build();
            String json = JsonUtil.toJsonString(new JsonAdaptedPerson(original));
            JsonAdaptedPerson restored = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class);
            assertTrue(json.contains("\"remark\""));
            assertEquals(original, restored.toModelType());
        }
    }

    @Test
    public void jsonAdaptedPerson_missingOrNullRemark_defaultsToEmpty() throws Exception {
        String legacyJson = "{\"name\":\"" + VALID_NAME + "\",\"studentId\":\"" + VALID_STUDENT_ID
                + "\",\"email\":\"" + VALID_EMAIL + "\",\"tags\":[]}";
        Person expected = new PersonBuilder(BENSON).withTags().withRemark("").build();
        assertEquals(expected, JsonUtil.fromJsonString(legacyJson, JsonAdaptedPerson.class).toModelType());
        String nullRemarkJson = legacyJson.substring(0, legacyJson.length() - 1) + ",\"remark\":null}";
        assertEquals(expected, JsonUtil.fromJsonString(nullRemarkJson, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void jsonAdaptedPerson_legacyContactFields_ignoredAndNotSaved() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        String legacyJson = json.substring(0, json.lastIndexOf('}'))
                + ",\"phone\":\"98765432\",\"address\":\"Old address\"}";
        Person restored = JsonUtil.fromJsonString(legacyJson, JsonAdaptedPerson.class).toModelType();
        assertEquals(BENSON, restored);
        String savedJson = JsonUtil.toJsonString(new JsonAdaptedPerson(restored));
        assertFalse(savedJson.contains("\"phone\""));
        assertFalse(savedJson.contains("\"address\""));
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void jsonAdaptedPerson_personWithLowercaseStudentId_serializesNormalizedStudentId() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(new PersonBuilder(BENSON)
                .withStudentId(VALID_STUDENT_ID.toLowerCase()).build());
        String jsonString = JsonUtil.toJsonString(person);

        assertTrue(jsonString.contains("\"studentId\" : \"" + VALID_STUDENT_ID + "\""));
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_STUDENT_ID, VALID_EMAIL,
                        VALID_TAGS, "");
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_STUDENT_ID, VALID_EMAIL, VALID_TAGS, "");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidStudentId_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_STUDENT_ID, VALID_EMAIL,
                        VALID_TAGS, "");
        String expectedMessage = StudentId.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullStudentId_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL,
                VALID_TAGS, "");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, StudentId.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_STUDENT_ID, INVALID_EMAIL,
                        VALID_TAGS, "");
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_STUDENT_ID, null, VALID_TAGS, "");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_STUDENT_ID, VALID_EMAIL,
                        invalidTags, "");
        assertThrows(IllegalValueException.class, person::toModelType);
    }

}
