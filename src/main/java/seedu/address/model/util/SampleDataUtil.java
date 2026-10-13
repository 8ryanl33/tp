package seedu.address.model.util;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    /**
     * Returns sample students with identifying information.
     */
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new StudentId("A7654321B"),
                new Email("alexyeoh@example.com")),
            new Person(new Name("Bernice Yu"), new StudentId("B7654321C"),
                new Email("berniceyu@example.com")),
            new Person(new Name("Charlotte Oliveiro"), new StudentId("C7654321D"),
                new Email("charlotte@example.com")),
            new Person(new Name("David Li"), new StudentId("D7654321E"),
                new Email("lidavid@example.com")),
            new Person(new Name("Irfan Ibrahim"), new StudentId("E7654321F"),
                new Email("irfan@example.com")),
            new Person(new Name("Roy Balakrishnan"), new StudentId("F7654321G"),
                new Email("royb@example.com"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

}
