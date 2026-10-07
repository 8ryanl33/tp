package seedu.address.logic;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_SAVE_FAILURE = "Unable to save student data.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} that saves changes before publishing them.
     *
     * @param model The live model.
     * @param storage The storage used for saving changes.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    /**
     * {@inheritDoc}
     * Executes modifying commands against a temporary model and publishes their changes only after saving succeeds.
     * Commands that do not modify student data execute without saving.
     */
    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);
        if (!command.isModifyingData()) {
            return command.execute(model);
        }
        return executeAndSave(command);
    }

    private CommandResult executeAndSave(Command command) throws CommandException {
        Model pendingModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        pendingModel.updateFilteredPersonList(model.getFilteredPersonListPredicate()::test);
        CommandResult result = command.execute(pendingModel);
        saveStudentData(pendingModel);
        model.setAddressBook(pendingModel.getAddressBook());
        model.updateFilteredPersonList(pendingModel.getFilteredPersonListPredicate()::test);
        return result;
    }

    private void saveStudentData(Model pendingModel) throws CommandException {
        try {
            storage.saveAddressBook(pendingModel.getAddressBook());
        } catch (IOException ioe) {
            logger.warning("Unable to save student data: " + ioe.getMessage());
            throw new CommandException(MESSAGE_SAVE_FAILURE, ioe);
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
