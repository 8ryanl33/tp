package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RemarkContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        RemarkContainsKeywordsPredicate predicate = new RemarkContainsKeywordsPredicate(List.of("recursion"));

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new RemarkContainsKeywordsPredicate(List.of("recursion"))));
        assertFalse(predicate.equals(new RemarkContainsKeywordsPredicate(List.of("loops"))));
        assertFalse(predicate.equals(new NameContainsKeywordsPredicate(List.of("recursion"))));
        assertFalse(predicate.equals(null));
    }

    @Test
    public void test_remarkContainsKeyword_returnsTrue() {
        // EP: whole-word/partial matches, case variation, and numeric slash text.
        Person person = new PersonBuilder().withRemark("Needs help with recursion. Quiz: 8/10.").build();

        assertTrue(new RemarkContainsKeywordsPredicate(List.of("recursion")).test(person));
        assertTrue(new RemarkContainsKeywordsPredicate(List.of("ReCuRsIoN")).test(person));
        assertTrue(new RemarkContainsKeywordsPredicate(List.of("curs")).test(person));
        assertTrue(new RemarkContainsKeywordsPredicate(List.of("8/10")).test(person));
    }

    @Test
    public void test_onlyOneOfMultipleKeywordsMatches_returnsTrue() {
        // EP: OR matching succeeds regardless of matching keyword position.
        Person person = new PersonBuilder().withRemark("Needs help with recursion").build();

        assertTrue(new RemarkContainsKeywordsPredicate(List.of("loops", "recursion")).test(person));
        assertTrue(new RemarkContainsKeywordsPredicate(List.of("recursion", "loops")).test(person));
    }

    @Test
    public void test_noMatchingKeywords_returnsFalse() {
        // EP: no selected-field match or no supplied keywords.
        Person person = new PersonBuilder().withRemark("Needs help with recursion").build();

        assertFalse(new RemarkContainsKeywordsPredicate(List.of("loops", "arrays")).test(person));
        assertFalse(new RemarkContainsKeywordsPredicate(List.of()).test(person));
    }

    @Test
    public void test_emptyRemark_returnsFalse() {
        // EP: an empty stored remark cannot match a nonempty keyword.
        Person person = new PersonBuilder().withRemark("").build();

        assertFalse(new RemarkContainsKeywordsPredicate(List.of("recursion", "loops")).test(person));
    }

    @Test
    public void test_keywordsMatchOtherFieldsOnly_returnsFalse() {
        // EP: matches in other fields must not affect a remark search.
        Person person = new PersonBuilder().withName("Alice").withStudentId("A1234567B")
                .withEmail("alice@example.com").withLabels("recursion").withRemark("Needs help with loops").build();

        assertFalse(new RemarkContainsKeywordsPredicate(
                List.of("Alice", "A1234567B", "example.com", "recursion")).test(person));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("recursion", "loops");
        RemarkContainsKeywordsPredicate predicate = new RemarkContainsKeywordsPredicate(keywords);

        String expected = RemarkContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
