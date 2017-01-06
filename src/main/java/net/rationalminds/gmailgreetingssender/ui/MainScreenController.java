/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.rationalminds.gmailgreetingssender.biz.BusinessHelper;
import net.rationalminds.gmailgreetingssender.biz.MassEmailService;
import net.rationalminds.gmailgreetingssender.ui.data.DynamicContactsFromCsv;
import net.rationalminds.gmailgreetingssender.ui.data.MailDetails;
import net.rationalminds.gmailgreetingssender.ui.data.MessageBoard;
import net.rationalminds.gmailgreetingssender.utils.BadCsvFileException;
import net.rationalminds.gmailgreetingssender.utils.Constants;
import net.rationalminds.gmailgreetingssender.utils.Utilities;

/**
 *
 * @author Vaibhav Singh
 */
public class MainScreenController implements Initializable {

    private final MailDetails details = new MailDetails();
    private final MessageBoard msgBoard = new MessageBoard();

    private final Map<String, String> errorMessages = new HashMap<>();
    private final StringProperty disableApplicationCmd = new SimpleStringProperty("enabled");

    @FXML
    TextField gmailUserName;
    @FXML
    TextField gmailPassword;
    @FXML
    TextField emailSubject;
    @FXML
    TextArea emailBody;
    @FXML
    Text contactFilePath;
    @FXML
    Text attachedFileNames;
    @FXML
    Text validationResultsText;
    @FXML
    Button massMailButton;
    @FXML
    Button testMailButton;
    @FXML
    TextField disableApplication;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gmailUserName.textProperty().bindBidirectional(details.gmailUserNameProperty());
        gmailPassword.textProperty().bindBidirectional(details.gmailPasswordProperty());
        emailSubject.textProperty().bindBidirectional(details.emailSubjectProperty());
        emailBody.textProperty().bindBidirectional(details.emailBodyProperty());
        contactFilePath.textProperty().bindBidirectional(details.contactFilePathProperty());
        attachedFileNames.textProperty().bindBidirectional(details.attachedFileNamesProperty());
        emailSubject.textProperty().bindBidirectional(details.emailSubjectProperty());
        disableApplication.textProperty().bindBidirectional(disableApplicationCmd);

        gmailUserName.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!gmailUserName.getText().matches(Constants.GMAIL_SRING_PATTERN)) {
                    gmailUserName.setStyle("-fx-text-fill: red;");
                    errorMessages.put(gmailUserName.getId(), "Put valid Gmail ID. Example someone@gmail.com");
                } else {
                    gmailUserName.setStyle("");
                    errorMessages.remove(gmailUserName.getId());
                }
            }
            showMessages();
        });
    }

    /**
     *
     */
    private void showMessages() {
        if (errorMessages.isEmpty()) {
            validationResultsText.setText("");
        } else {
            String msg = "";
            int i = 0;
            for (String m : errorMessages.values()) {
                i++;
                msg = msg + m;
                msg = errorMessages.size() == i ? msg : msg + "\n";
            }
            validationResultsText.setText(msg);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void chooseContactsFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("open contacts file (csv)");
        chooser.setInitialDirectory(new File(Utilities.getOpenDialogInitialDir()));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("comma seperated value", "*.csv"));
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                contactFilePath.setStyle("");
                DynamicContactsFromCsv contacts = BusinessHelper.getContacts(file);
                details.setContacts(contacts);
                String msg = contacts.getRecepients().size() == 1 ? "contact" : "contacts";
                msg = contacts.getRecepients().size() + " " + msg + " found in file " + file.getName();
                details.contactFilePathProperty().set(msg);
            } catch (BadCsvFileException ex) {
                details.contactFilePathProperty().set(ex.getMessage());
                contactFilePath.setStyle("-fx-fill: red;");
            }
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void chosseAttachments(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("open contacts file (csv)");
        chooser.setInitialDirectory(new File(Utilities.getOpenDialogInitialDir()));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("images (jpeg, jpg, gif, png, tif)", "*.jpeg", "*.jpg", "*.png", "*.tif", "*.gif"),
                new FileChooser.ExtensionFilter("pdf (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("documents (ecxel,text, documents, ppt)", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        List<File> files = chooser.showOpenMultipleDialog(new Stage());
        msgBoard.appendMessage("Selecting attachments for email");
        if (files != null) {
            if (files.size() > 3) {
                msgBoard.appendMessage("You have selected " + files.size() + " attachments. Only 3 are allowed with this version of mass mailer.");
                details.setAttachedFileNames("You are only allowed to select maximum 3 attachments");
                attachedFileNames.setStyle("-fx-fill: red;");
            } else {
                String fileNames = "";
                for (File f : files) {
                    fileNames = "".equals(fileNames) ? "" : fileNames + ",";
                    fileNames = fileNames + f.getName();
                    msgBoard.appendMessage("Attachment : " + f.getPath());
                    if (fileNames.length() > 50) {
                        fileNames = fileNames + "...";
                        break;
                    }
                    attachedFileNames.setStyle("");
                }
                details.setAttachedFileNames(fileNames);
                details.setAttachments(files);
            }
        }
    }

    @FXML
    protected void sendMassEmails(ActionEvent event) throws IOException {
        try {
            disableApplicationCmd.set("");
            msgBoard.appendMessage("Send button will be disabled now. If you want to resend, restart the application.");
            testMailButton.setText("disabled");
            massMailButton.setText("disabled");
            (new Thread(new MassEmailService(details))).start();
        } catch (Exception ex) {
            msgBoard.appendMessage("Application error! " + ex.getMessage());
            msgBoard.appendMessage("Please try again after restarting aplication");
        }
    }

    @FXML
    protected void sendTestEmail(ActionEvent event) throws IOException {
        try {
            BusinessHelper.sendTestEmail(details);
        } catch (Exception ex) {
            msgBoard.appendMessage("Application error! " + ex.getMessage());
            msgBoard.appendMessage("Please try again after restarting aplication");
        }
    }
}
