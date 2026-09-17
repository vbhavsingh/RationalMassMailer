/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import net.rationalminds.massmailer.biz.BusinessHelper;
import net.rationalminds.massmailer.biz.MassEmailService;
import net.rationalminds.massmailer.biz.SmtpSessionService;
import net.rationalminds.massmailer.ui.data.DynamicContactsFromCsv;
import net.rationalminds.massmailer.ui.data.MailDetails;
import net.rationalminds.massmailer.ui.data.MessageBoard;
import net.rationalminds.massmailer.utils.BadCsvFileException;
import net.rationalminds.massmailer.utils.Constants;
import net.rationalminds.massmailer.utils.Utilities;

/**
 *
 * @author Vaibhav Singh
 */
public class MainScreenController implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final MailDetails details = new MailDetails();
    private final MessageBoard msgBoard = MessageBoard.getMessageBoard();

    private final Map<String, String> errorMessages = new HashMap<>();
    private final StringProperty disableApplicationCmd = new SimpleStringProperty("enabled");
    
    private final StringProperty mainScreenLiveMessageProperty = new SimpleStringProperty("");
    
    private final BooleanProperty disableResumeBtnProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty disablePauseBtnProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty lockScreen = new SimpleBooleanProperty(false);

    @FXML
    ComboBox<String> mailProvider;
    @FXML
    Label emailUserNameLabel;
    @FXML
    TextField emailUserName;
    @FXML
    Label emailPasswordLabel;
    @FXML
    TextField emailPassword;
    @FXML
    Label smtp2GoFromEmailLabel;
    @FXML
    TextField smtp2GoFromEmail;
    @FXML
    Label smtp2GoPortLabel;
    @FXML
    TextField smtp2GoPort;
    @FXML
    Label smtp2GoTestRecipientLabel;
    @FXML
    TextField smtp2GoTestRecipient;
    @FXML
    TextField emailSubject;
   /* @FXML
    TextArea emailBody;*/
    @FXML
    TabPane mailBodyTabPane;
    @FXML
    Tab htmlCodeTab;
    @FXML
    Tab formattedTab;
    @FXML
    TextArea htmlEmailBody;
    @FXML
    HTMLEditor htmlWysiwygEditor;
    @FXML
    TextField mailsPerHour;
    @FXML
    Text contactFilePath;
    @FXML
    Text attachedFileNames;
    @FXML
    Text mainScreenLiveMessage;
    @FXML
    Button chooseContactsFileButton;
    @FXML
    Button chosseAttachmentsButton;
    @FXML
    Button importTemplateButton;
    @FXML
    Button showSampleButton;
    @FXML
    Button massMailButton;
    @FXML
    Button testMailButton;
    @FXML
    Button pauseMailButton;
    @FXML
    Button resumeMailButton;
    @FXML
    TextField disableApplication;

   

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
        mailProvider.getItems().addAll(Constants.MAIL_PROVIDER_AUTO, Constants.MAIL_PROVIDER_SMTP2GO);
        mailProvider.valueProperty().bindBidirectional(details.mailProviderProperty());
        mailProvider.setValue(details.getMailProvider());
        mailProvider.disableProperty().bindBidirectional(lockScreen);

        emailUserName.textProperty().bindBidirectional(details.emailUserNameProperty());
        emailUserName.disableProperty().bindBidirectional(lockScreen);
        
        emailPassword.textProperty().bindBidirectional(details.emailPasswordProperty());
        emailPassword.disableProperty().bindBidirectional(lockScreen);

        smtp2GoFromEmail.textProperty().bindBidirectional(details.smtp2GoFromEmailProperty());
        smtp2GoFromEmail.disableProperty().bindBidirectional(lockScreen);

        smtp2GoPort.textProperty().bindBidirectional(details.smtp2GoPortProperty());
        smtp2GoPort.disableProperty().bindBidirectional(lockScreen);

        smtp2GoTestRecipient.textProperty().bindBidirectional(details.smtp2GoTestRecipientProperty());
        smtp2GoTestRecipient.disableProperty().bindBidirectional(lockScreen);

        updateProviderFields();

        emailSubject.textProperty().bindBidirectional(details.emailSubjectProperty());
        emailSubject.disableProperty().bindBidirectional(lockScreen);
        
       /* emailBody.textProperty().bindBidirectional(details.emailBodyProperty());*/
        htmlEmailBody.textProperty().bindBidirectional(details.htmlEmailBodyProperty());
        htmlEmailBody.disableProperty().bindBidirectional(lockScreen);
        htmlWysiwygEditor.disableProperty().bindBidirectional(lockScreen);
        htmlWysiwygEditor.setHtmlText(details.getHtmlEmailBody());

        //Keep the WYSIWYG "Formatted" view and the raw "HTML Code" view in sync
        //whenever the user switches between them, since HTMLEditor has no
        //observable text property to bind directly.
        mailBodyTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if (newTab == formattedTab) {
                    htmlWysiwygEditor.setHtmlText(details.getHtmlEmailBody());
                } else if (newTab == htmlCodeTab) {
                    details.setHtmlEmailBody(htmlWysiwygEditor.getHtmlText());
                }
            }
        });

        contactFilePath.textProperty().bindBidirectional(details.contactFilePathProperty());
        attachedFileNames.textProperty().bindBidirectional(details.attachedFileNamesProperty());
        emailSubject.textProperty().bindBidirectional(details.emailSubjectProperty());
        
        mailsPerHour.textProperty().bindBidirectional(details.mailsPerHourProperty());
        disableApplication.textProperty().bindBidirectional(disableApplicationCmd);
        
        pauseMailButton.disableProperty().bindBidirectional(disablePauseBtnProperty);
        resumeMailButton.disableProperty().bindBidirectional(disableResumeBtnProperty);
        
        chooseContactsFileButton.disableProperty().bindBidirectional(lockScreen);
        chosseAttachmentsButton.disableProperty().bindBidirectional(lockScreen);
        importTemplateButton.disableProperty().bindBidirectional(lockScreen);
        showSampleButton.disableProperty().bindBidirectional(lockScreen);
        
        mainScreenLiveMessage.textProperty().bind(mainScreenLiveMessageProperty);
        
        msgBoard.appendMessage("Starting applicaton..");

        //Validations
        emailUserName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != oldValue) {
                    validateEmailUserName();
                }
                showMessages();
            }

        });

        mailProvider.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateProviderFields();
                validateEmailUserName();
                validateSmtp2GoFromEmail();
                validateSmtp2GoTestRecipient();
                showMessages();
            }

        });

        smtp2GoFromEmail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateSmtp2GoFromEmail();
                showMessages();
            }

        });

        smtp2GoTestRecipient.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateSmtp2GoTestRecipient();
                showMessages();
            }

        });

        emailPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (newValue.length() < 8) {
                    emailPassword.setStyle("-fx-text-fill: red;");
                    errorMessages.put(emailPassword.getId(), "Mail passwords are minumum 8 character long.");
                } else {
                    emailPassword.setStyle("");
                    errorMessages.remove(emailPassword.getId());
                }

                showMessages();
            }

        });
        
        mailsPerHour.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				newValue = newValue.replaceAll("[^\\d]", "");
				if(newValue==null || newValue.length()==0) {
					newValue = "";
				}else {
					int val = Integer.parseInt(newValue);
					if(val ==0 ) {
						newValue = "1";
					}
					if(val >999 ) {
						newValue = "999";
					}
				}
				mailsPerHour.setText(newValue);

			}
		});

        smtp2GoPort.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String digitsOnly = newValue.replaceAll("[^\\d]", "");
				if (digitsOnly.length() > 5) {
					digitsOnly = digitsOnly.substring(0, 5);
				}
				if (!digitsOnly.isEmpty()) {
					int val = Integer.parseInt(digitsOnly);
					if (val == 0) {
						digitsOnly = "1";
					}
					if (val > 65535) {
						digitsOnly = "65535";
					}
				}
				if (!digitsOnly.equals(newValue)) {
					smtp2GoPort.setText(digitsOnly);
				}
			}
		});
    }

    /**
     * Validates the "From Email ID" field. For SMTP2GO this is the SMTP
     * account username (e.g. an account or domain name, not necessarily an
     * email address), so it is only required to be non-blank; the actual
     * sender address is validated separately in {@link #validateSmtp2GoFromEmail()}.
     */
    private void validateEmailUserName() {
        boolean isSmtp2Go = Constants.MAIL_PROVIDER_SMTP2GO.equals(mailProvider.getValue());
        boolean valid = isSmtp2Go
                ? !emailUserName.getText().trim().isEmpty()
                : emailUserName.getText().matches(Constants.PERMITTED_EMAIL_PATTERN);
        if (!valid) {
            emailUserName.setStyle("-fx-text-fill: red;");
            String message = isSmtp2Go
                    ? "Provide your SMTP2GO username."
                    : "Provide valid Gmail or Yahoo id! Always use full email id.";
            errorMessages.put(emailUserName.getId(), message);
        } else {
            emailUserName.setStyle("");
            errorMessages.remove(emailUserName.getId());
        }
    }

    /**
     * Validates the "SMTP2GO From Email" field, which only applies when
     * SMTP2GO is the selected provider: SMTP2GO relays mail on behalf of any
     * verified sender address, which is unrelated to the SMTP account
     * username entered above.
     */
    private void validateSmtp2GoFromEmail() {
        if (!Constants.MAIL_PROVIDER_SMTP2GO.equals(mailProvider.getValue())) {
            smtp2GoFromEmail.setStyle("");
            errorMessages.remove(smtp2GoFromEmail.getId());
            return;
        }
        if (!smtp2GoFromEmail.getText().matches(Constants.EMAIL_REGEX_PATERN)) {
            smtp2GoFromEmail.setStyle("-fx-text-fill: red;");
            errorMessages.put(smtp2GoFromEmail.getId(), "Provide a valid, full email id to send from via SMTP2GO.");
        } else {
            smtp2GoFromEmail.setStyle("");
            errorMessages.remove(smtp2GoFromEmail.getId());
        }
    }

    /**
     * Validates the "Send Test To" field, which only applies when SMTP2GO
     * is selected: SMTP2GO "From" addresses are frequently send-only, so
     * the test send needs a separate, real recipient address.
     */
    private void validateSmtp2GoTestRecipient() {
        if (!Constants.MAIL_PROVIDER_SMTP2GO.equals(mailProvider.getValue())) {
            smtp2GoTestRecipient.setStyle("");
            errorMessages.remove(smtp2GoTestRecipient.getId());
            return;
        }
        if (!smtp2GoTestRecipient.getText().matches(Constants.EMAIL_REGEX_PATERN)) {
            smtp2GoTestRecipient.setStyle("-fx-text-fill: red;");
            errorMessages.put(smtp2GoTestRecipient.getId(), "Provide a real, valid email id to receive the test send.");
        } else {
            smtp2GoTestRecipient.setStyle("");
            errorMessages.remove(smtp2GoTestRecipient.getId());
        }
    }

    /**
     * "From Email ID" and "Email Password" mean something different for
     * SMTP2GO (an SMTP account username/password, unrelated to the actual
     * sender mailbox) than for Gmail/Yahoo (the mailbox's own login), so
     * their labels and prompts switch with the selected provider. The
     * SMTP2GO From Email field only makes sense for SMTP2GO, so it is
     * hidden the rest of the time.
     */
    private void updateProviderFields() {
        boolean isSmtp2Go = Constants.MAIL_PROVIDER_SMTP2GO.equals(mailProvider.getValue());

        emailUserNameLabel.setText(isSmtp2Go ? "SMTP2GO Username*:" : "From Email ID*:");
        emailUserName.setPromptText(isSmtp2Go ? "Enter your SMTP2GO account username" : "Enter full gmail or yahoo id");

        emailPasswordLabel.setText(isSmtp2Go ? "SMTP2GO Password*:" : "Email Password*:");

        smtp2GoFromEmailLabel.setVisible(isSmtp2Go);
        smtp2GoFromEmailLabel.setManaged(isSmtp2Go);
        smtp2GoFromEmail.setVisible(isSmtp2Go);
        smtp2GoFromEmail.setManaged(isSmtp2Go);

        smtp2GoPortLabel.setVisible(isSmtp2Go);
        smtp2GoPortLabel.setManaged(isSmtp2Go);
        smtp2GoPort.setVisible(isSmtp2Go);
        smtp2GoPort.setManaged(isSmtp2Go);

        smtp2GoTestRecipientLabel.setVisible(isSmtp2Go);
        smtp2GoTestRecipientLabel.setManaged(isSmtp2Go);
        smtp2GoTestRecipient.setVisible(isSmtp2Go);
        smtp2GoTestRecipient.setManaged(isSmtp2Go);
    }

    /**
     *
     */
    private void showMessages() {
        if (errorMessages.isEmpty()) {
        	mainScreenLiveMessageProperty.set("");
            if ("disabled".equals(massMailButton.getText())) {
                disableApplicationCmd.set("");
            } else {
                disableApplicationCmd.set("enabled");
            }
        } else {
            disableApplicationCmd.set("");
            String msg = "";
            int i = 0;
            for (String m : errorMessages.values()) {
                i++;
                msg = msg + m;
                msg = errorMessages.size() == i ? msg : msg + "\n";
            }
            mainScreenLiveMessageProperty.set(msg);
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
        toggleControls(true,true);
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
        LOGGER.info("Selecting attachments for email");
        if (files != null) {
            if (files.size() > 3) {
            	String message = "You have selected " + files.size() + " attachments. Only 3 are allowed with this version of mass mailer.";
            	LOGGER.log(Level.WARNING,message);
                msgBoard.appendMessage(message);
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
                    LOGGER.info("attachement added : "+ f.getPath());
                }
                details.setAttachedFileNames(fileNames);
                details.setAttachments(files);
            }
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void importTemplate(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("open mail template (txt or html)");
        chooser.setInitialDirectory(new File(Utilities.getOpenDialogInitialDir()));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("text/html templates (*.txt, *.html, *.htm)", "*.txt", "*.html", "*.htm"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                String content = Utilities.readTextFile(file);
                showFormattedPreview(content);
                msgBoard.appendMessage("Loaded mail template from file: " + file.getPath());
                LOGGER.info("Loaded mail template from file: " + file.getPath());
            } catch (IOException ex) {
                String message = "Could not read template file " + file.getName() + ": " + ex.getMessage();
                LOGGER.log(Level.WARNING, message);
                msgBoard.appendMessage(message);
            }
        }
    }

    /**
     * Sets the mail body and immediately switches to the "Formatted" tab so
     * the rendered HTML is visible right away.
     */
    private void showFormattedPreview(String htmlContent) {
        details.setHtmlEmailBody(htmlContent);
        htmlWysiwygEditor.setHtmlText(htmlContent);
        mailBodyTabPane.getSelectionModel().select(formattedTab);
    }

    /**
     * Loads the built-in sample email so users can see what a properly
     * formatted mail template looks like in the text area.
     *
     * @param event
     */
    @FXML
    protected void showSampleTemplate(ActionEvent event) {
        try {
            String content = Utilities.readClasspathResource(getClass(), "/templates/sample-email-template.html");
            showFormattedPreview(content);
            msgBoard.appendMessage("Loaded the built-in sample email template.");
            LOGGER.info("Loaded the built-in sample email template.");
        } catch (IOException ex) {
            String message = "Could not load sample template: " + ex.getMessage();
            LOGGER.log(Level.WARNING, message);
            msgBoard.appendMessage(message);
        }
    }

    @FXML
    protected void sendMassEmails(ActionEvent event) throws IOException {
        try {
            disableApplicationCmd.set("");
            msgBoard.appendMessage("Send button will be disabled now. If you want to resend, restart the application.");
            (new Thread(new MassEmailService(details, mainScreenLiveMessageProperty,mainScreenLiveMessage))).start();
            toggleControls(false, true);
            pauseMailButton.setDisable(false);
            lockScreen.set(true);
        } catch (Exception ex) {
            msgBoard.appendMessage("Application error! " + ex.getMessage());
            msgBoard.appendMessage("Please try again after restarting aplication");
        }
    }

    @FXML
    protected void sendTestEmail(ActionEvent event) throws IOException {
        try {
        	String testRecipient = SmtpSessionService.getTestRecipient(details);
        	mainScreenLiveMessage.setStyle("-fx-fill: blue;");
        	mainScreenLiveMessageProperty.set("Sending test mail to : "+testRecipient);
        	Thread.sleep(1000);
            BusinessHelper.sendTestEmail(details);
            mainScreenLiveMessage.setStyle("-fx-fill: green;");
            mainScreenLiveMessageProperty.set("Send test mail to : "+testRecipient);
        } catch (Exception ex) {
            msgBoard.appendMessage("Application error! " + ex.getMessage());
            msgBoard.appendMessage("Please try again after restarting aplication");
            mainScreenLiveMessage.setStyle("-fx-fill: red;");
            mainScreenLiveMessageProperty.set("Test mail failed : "+ex.getMessage());
        }
    }
    
    
    @FXML
    protected void pauseMailSend(ActionEvent event) throws IOException {
    	MassEmailService.pauseMailSend();
        toggleControls(true, false);
        msgBoard.appendMessage("Mail send is paused");
        LOGGER.info("Mail send is paused");
    }
    
    @FXML
    protected void resumeMailSend(ActionEvent event) throws IOException {
    	MassEmailService.resumeMailSend();
    	toggleControls(false, true);
    	msgBoard.appendMessage("Mail send is resumed");
        LOGGER.info("Mail send is resumed");
    }
    
    private void toggleControls(boolean disablePause, boolean disableResume) {
    	 this.disablePauseBtnProperty.set(disablePause);
         this.disableResumeBtnProperty.set(disableResume);
    }
}
