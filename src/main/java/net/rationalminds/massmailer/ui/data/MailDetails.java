/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui.data;

import java.io.File;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

/**
 *
 * @author Vaibhav Singh
 */
public class MailDetails {

    private final StringProperty emailUserName;

    private final StringProperty emailPassword;

    private final StringProperty emailSubject;
    
    private final StringProperty htmlEmailBody;

    private final StringProperty contactFilePath;

    private final StringProperty attachedFileNames;
    
    private final StringProperty mailsPerHour;

    private DynamicContactsFromCsv contacts;

    private List<File> attachments;

    public MailDetails() {
        this.emailUserName = new SimpleStringProperty("");
        this.emailPassword = new SimpleStringProperty("");
        this.emailSubject = new SimpleStringProperty("");
        this.htmlEmailBody = new SimpleStringProperty("Paste formatted HTML content here and delete this message."
        		+ "\n\n"
        		+ "\nContent must be enclosed in <HTML></HTML> for proper look and feel."
        		+ "\n\n"
        		+ "\nYou can format your mail online, at https://html-online.com/editor"
        		+ "\n\n\n"
        		+ "Images can be included as links from the internet or as local files. See Help for details."
        		+ "\n\n"
        		+ "Yahoo and Google allows around 120 mails per day. "
        		+ "\nChoose \"mails per hour\" value properly to remian under this limit.");
        this.contactFilePath = new SimpleStringProperty("");
        this.mailsPerHour = new SimpleStringProperty("60");
        this.attachedFileNames = new SimpleStringProperty("Maximum 3 files are allowed.");
    }

    public String geEmailUserName() {
        return emailUserName.get();
    }

    public void setEmailUserName(String emailUserName) {
        this.emailUserName.set(emailUserName);
    }

    public StringProperty emailUserNameProperty() {
        return emailUserName;
    }

    public String getEmailPassword() {
        return emailPassword.get();
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword.set(emailPassword);
    }

    public StringProperty emailPasswordProperty() {
        return emailPassword;
    }

    public String getEmailSubject() {
        return emailSubject.get();
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject.set(emailSubject);
    }

    public StringProperty emailSubjectProperty() {
        return emailSubject;
    }

    public String getMailsPerHour() {
        return mailsPerHour.get();
    }

    public void setMailsPerHour(String mailsPerHour) {
        this.mailsPerHour.set(mailsPerHour);
    }

    public StringProperty mailsPerHourProperty() {
        return mailsPerHour;
    }
    
    
    public String getHtmlEmailBody() {
        return htmlEmailBody.get();
    }

    public void setHtmlEmailBody(String htmlEmailBody) {
        this.htmlEmailBody.set(htmlEmailBody);
    }

    public StringProperty htmlEmailBodyProperty() {
        return htmlEmailBody;
    }

	public String getContactFilePath() {
        return contactFilePath.get();
    }

    public void setContactFilePath(String contactFilePath) {
        this.contactFilePath.set(contactFilePath);
    }

    public StringProperty contactFilePathProperty() {
        return contactFilePath;
    }

    public String getAttachedFileNames() {
        return attachedFileNames.get();
    }

    public void setAttachedFileNames(String attachedFileNames) {
        this.attachedFileNames.set(attachedFileNames);
    }

    public StringProperty attachedFileNamesProperty() {
        return attachedFileNames;
    }

    public DynamicContactsFromCsv getContacts() {
        return contacts;
    }

    public void setContacts(DynamicContactsFromCsv contacts) {
        this.contacts = contacts;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<File> attachments) {
        this.attachments = attachments;
    }
}
