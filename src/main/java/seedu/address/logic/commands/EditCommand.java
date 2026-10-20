package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LABEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.label.Label;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.model.person.StudentId;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by their Student ID. Add labels with l/ and remove them with dl/. "
            + "An empty l/ clears all labels; an empty r/ clears the remark.\n"
            + "Parameters: " + PREFIX_STUDENT_ID + "STUDENT_ID "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_REMARK + "REMARK] "
            + "[" + PREFIX_LABEL + "LABEL_NAME]... "
            + "[" + PREFIX_DELETE_LABEL + "LABEL_NAME]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_STUDENT_ID + "A0123456B "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_STUDENT_NOT_FOUND = "Student with ID %s is not in the records.";
    public static final String MESSAGE_LABEL_NOT_FOUND = "Student %1$s does not have label \"%2$s\".";
    public static final String MESSAGE_DUPLICATE_LABEL = "Student %1$s already has label \"%2$s\".";

    private final StudentId targetStudentId;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param studentId The Student ID of the student to edit.
     * @param editPersonDescriptor The details to edit.
     */
    public EditCommand(StudentId studentId, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(studentId);
        requireNonNull(editPersonDescriptor);

        this.targetStudentId = studentId;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToEdit = findStudent(model);
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    private Person findStudent(Model model) throws CommandException {
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getStudentId().equals(targetStudentId))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND, targetStudentId)));
    }

    @Override
    public boolean isModifyingData() {
        return true;
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        StudentId updatedStudentId = personToEdit.getStudentId();
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Remark updatedRemark = editPersonDescriptor.getRemark().orElse(personToEdit.getRemark());
        Set<Label> updatedLabels = updateLabels(personToEdit, editPersonDescriptor);

        return new Person(updatedName, updatedStudentId, updatedEmail,
                updatedLabels, updatedRemark);
    }

    private static Set<Label> updateLabels(Person person, EditPersonDescriptor descriptor) throws CommandException {
        if (descriptor.isClearingLabels()) {
            return Set.of();
        }

        Set<Label> labels = new HashSet<>(person.getLabels());
        for (Label labelToRemove : descriptor.getLabelsToRemove()) {
            Label existingLabel = findMatchingLabel(labels, labelToRemove).orElseThrow(() ->
                    new CommandException(String.format(MESSAGE_LABEL_NOT_FOUND,
                            person.getStudentId(), labelToRemove.labelName)));
            labels.remove(existingLabel);
        }
        for (Label labelToAdd : descriptor.getLabelsToAdd()) {
            if (findMatchingLabel(labels, labelToAdd).isPresent()) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE_LABEL,
                        person.getStudentId(), labelToAdd.labelName));
            }
            labels.add(labelToAdd);
        }
        return labels;
    }

    private static Optional<Label> findMatchingLabel(Set<Label> labels, Label target) {
        String normalizedTargetName = target.labelName.toLowerCase(Locale.ROOT);
        return labels.stream()
                .filter(label -> label.labelName.toLowerCase(Locale.ROOT).equals(normalizedTargetName))
                .findFirst();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return targetStudentId.equals(otherEditCommand.targetStudentId)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetStudentId", targetStudentId)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores replacement fields and label operations for editing a student.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Email email;
        private Remark remark;
        private List<Label> labelsToAdd = new ArrayList<>();
        private List<Label> labelsToRemove = new ArrayList<>();
        private boolean isClearingLabels;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setEmail(toCopy.email);
            setRemark(toCopy.remark);
            setLabelsToAdd(toCopy.labelsToAdd);
            setLabelsToRemove(toCopy.labelsToRemove);
            setClearingLabels(toCopy.isClearingLabels);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, email, remark)
                    || !labelsToAdd.isEmpty() || !labelsToRemove.isEmpty() || isClearingLabels;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        public void setLabelsToAdd(List<Label> labelsToAdd) {
            this.labelsToAdd = new ArrayList<>(requireNonNull(labelsToAdd));
        }

        public List<Label> getLabelsToAdd() {
            return List.copyOf(labelsToAdd);
        }

        public void setLabelsToRemove(List<Label> labelsToRemove) {
            this.labelsToRemove = new ArrayList<>(requireNonNull(labelsToRemove));
        }

        public List<Label> getLabelsToRemove() {
            return List.copyOf(labelsToRemove);
        }

        public void setClearingLabels(boolean isClearingLabels) {
            this.isClearingLabels = isClearingLabels;
        }

        public boolean isClearingLabels() {
            return isClearingLabels;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(remark, otherEditPersonDescriptor.remark)
                    && labelsToAdd.equals(otherEditPersonDescriptor.labelsToAdd)
                    && labelsToRemove.equals(otherEditPersonDescriptor.labelsToRemove)
                    && isClearingLabels == otherEditPersonDescriptor.isClearingLabels;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("email", email)
                    .add("remark", remark)
                    .add("labelsToAdd", labelsToAdd)
                    .add("labelsToRemove", labelsToRemove)
                    .add("isClearingLabels", isClearingLabels)
                    .toString();
        }
    }
}
