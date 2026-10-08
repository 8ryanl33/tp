package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a JSON file on the hard disk.
 */
public class JsonAddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private Path filePath;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableAddressBook> jsonAddressBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableAddressBook.class);
        if (!jsonAddressBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAddressBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     * Writes to a temporary file in the same directory, then atomically replaces the destination.
     * If writing or replacement fails, the previous data file remains unchanged.
     *
     * @param addressBook The non-null student records to save.
     * @param filePath location of the data. Cannot be null.
     * @throws IOException if writing fails or the filesystem does not support atomic replacement.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        Path targetFile = filePath.toAbsolutePath().normalize();
        Files.createDirectories(targetFile.getParent());
        Path temporaryFile = Files.createTempFile(targetFile.getParent(), "teachassist-", ".tmp");
        try {
            JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), temporaryFile);
            replaceDataFile(temporaryFile, targetFile);
        } finally {
            removeTemporaryFile(temporaryFile);
        }
    }

    /**
     * Atomically replaces the saved data with a completely written temporary file.
     *
     * @param temporaryFile The completed temporary file in the destination directory.
     * @param targetFile The destination file.
     * @throws IOException if atomic replacement fails, leaving the previous destination unchanged.
     */
    protected void replaceDataFile(Path temporaryFile, Path targetFile) throws IOException {
        Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    private void removeTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException e) {
            logger.warning("Unable to remove temporary data file " + temporaryFile + ": " + e.getMessage());
        }
    }

}
