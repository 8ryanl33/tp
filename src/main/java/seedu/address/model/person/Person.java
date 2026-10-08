package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.label.Label;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final StudentId studentId;
    private final Email email;

    // Data fields
    private final Remark remark;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Label> labels = new HashSet<>();

    /**
     * Constructs a person with an empty remark.
     * Every supplied field must be present and not null.
     */
    public Person(Name name, StudentId studentId, Email email, Set<Tag> tags) {
        this(name, studentId, email, tags, new Remark(""));
    }

    /**
     * Constructs a person with the supplied details and remark.
     * Every field must be present and not null.
     */
    public Person(Name name, StudentId studentId, Email email, Set<Tag> tags, Remark remark) {
        this(name, studentId, email, tags, Set.of(), remark);
    }

    /**
     * Constructs a person with the supplied details, labels, and remark.
     * Every field must be present and not null.
     */
    public Person(Name name, StudentId studentId, Email email, Set<Tag> tags, Set<Label> labels, Remark remark) {
        requireAllNonNull(name, studentId, email, tags, labels, remark);
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.remark = remark;
        this.tags.addAll(tags);
        this.labels.addAll(labels);
    }

    public Name getName() {
        return name;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public Email getEmail() {
        return email;
    }

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable label set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Label> getLabels() {
        return Collections.unmodifiableSet(labels);
    }

    /**
     * Returns true if both persons have the same student ID.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getStudentId().equals(getStudentId());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && studentId.equals(otherPerson.studentId)
                && email.equals(otherPerson.email)
                && remark.equals(otherPerson.remark)
                && tags.equals(otherPerson.tags)
                && labels.equals(otherPerson.labels);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, studentId, email, tags, labels, remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("studentId", studentId)
                .add("email", email)
                .add("tags", tags)
                .add("labels", labels)
                .add("remark", remark)
                .toString();
    }

}
