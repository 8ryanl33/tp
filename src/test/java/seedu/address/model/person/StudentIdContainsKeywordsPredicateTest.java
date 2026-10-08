package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class StudentIdContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        StudentIdContainsKeywordsPredicate firstPredicate =
                new StudentIdContainsKeywordsPredicate(firstPredicateKeywordList);
        StudentIdContainsKeywordsPredicate secondPredicate =
                new StudentIdContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        StudentIdContainsKeywordsPredicate firstPredicateCopy =
                new StudentIdContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different student ID keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_studentIdContainsKeywords_returnsTrue() {
        // One keyword
        StudentIdContainsKeywordsPredicate predicate = new StudentIdContainsKeywordsPredicate(List.of("A1234567B"));
        assertTrue(predicate.test(new PersonBuilder().withStudentId("A1234567B").build()));

        // Multiple keywords
        predicate = new StudentIdContainsKeywordsPredicate(List.of("A1234567B", "B1234567C"));
        assertTrue(predicate.test(new PersonBuilder().withStudentId("A1234567B").build()));

        // Only one matching keyword
        predicate = new StudentIdContainsKeywordsPredicate(List.of("B1234567C", "A1234567B"));
        assertTrue(predicate.test(new PersonBuilder().withStudentId("A1234567B").build()));

        // Mixed-case keywords
        predicate = new StudentIdContainsKeywordsPredicate(List.of("a1234567b", "b1234567c"));
        assertTrue(predicate.test(new PersonBuilder().withStudentId("A1234567B").build()));
    }

    @Test
    public void test_studentIdDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        StudentIdContainsKeywordsPredicate predicate = new StudentIdContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withStudentId("A1234567B").build()));

        // Non-matching keyword
        predicate = new StudentIdContainsKeywordsPredicate(List.of("C1234567D"));
        assertFalse(predicate.test(new PersonBuilder().withStudentId("A1234567B").build()));

        // Keywords match name and email, but do not match student ID
        predicate = new StudentIdContainsKeywordsPredicate(List.of("Alice", "alice@example.com"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withStudentId("A1234567B").withEmail("alice@example.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        StudentIdContainsKeywordsPredicate predicate = new StudentIdContainsKeywordsPredicate(keywords);

        String expected = StudentIdContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
