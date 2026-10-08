package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class EmailContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        EmailContainsKeywordsPredicate firstPredicate = new EmailContainsKeywordsPredicate(firstPredicateKeywordList);
        EmailContainsKeywordsPredicate secondPredicate = new EmailContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        EmailContainsKeywordsPredicate firstPredicateCopy = new EmailContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different email keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_emailContainsKeywords_returnsTrue() {
        // One keyword
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(List.of("alice"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Multiple keywords
        predicate = new EmailContainsKeywordsPredicate(List.of("alice", "example"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Only one matching keyword
        predicate = new EmailContainsKeywordsPredicate(List.of("bob", "example"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Mixed-case keywords
        predicate = new EmailContainsKeywordsPredicate(List.of("aLIce", "eXAMPLE"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));
    }

    @Test
    public void test_emailDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Non-matching keyword
        predicate = new EmailContainsKeywordsPredicate(List.of("carol"));
        assertFalse(predicate.test(new PersonBuilder().withEmail("alice@example.com").build()));

        // Keywords match name and student ID, but do not match email
        predicate = new EmailContainsKeywordsPredicate(List.of("Alice", "A1234567B"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withStudentId("A1234567B").withEmail("bob@example.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(keywords);

        String expected = EmailContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
