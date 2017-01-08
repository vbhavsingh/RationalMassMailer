/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.ui.data;

import java.io.File;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Vaibhav Singh
 */
public class MailDetails {

    private final StringProperty gmailUserName;

    private final StringProperty gmailPassword;

    private final StringProperty emailSubject;

    private final StringProperty emailBody;

    private final StringProperty contactFilePath;

    private final StringProperty attachedFileNames;

    private DynamicContactsFromCsv contacts;

    private List<File> attachments;

    public MailDetails() {
        this.gmailUserName = new SimpleStringProperty("");
        this.gmailPassword = new SimpleStringProperty("");
        this.emailSubject = new SimpleStringProperty("");
        this.emailBody = new SimpleStringProperty("Paste formatted HTML mail content. \nYou can format your mail online at https://html-online.com/editor");
        this.contactFilePath = new SimpleStringProperty("");
        this.attachedFileNames = new SimpleStringProperty("Maximum 3 files allowed.");
    }

    public String getGmailUserName() {
        return gmailUserName.get();
    }

    public void setGmailUserName(String gmailUserName) {
        this.gmailUserName.set(gmailUserName);
    }

    public StringProperty gmailUserNameProperty() {
        return gmailUserName;
    }

    public String getGmailPassword() {
        return gmailPassword.get();
    }

    public void setGmailPassword(String gmailPassword) {
        this.gmailPassword.set(gmailPassword);
    }

    public StringProperty gmailPasswordProperty() {
        return gmailPassword;
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

    public String getEmailBody() {
        return emailBody.get();
    }

    public void setEmailBody(String emailBody) {
        this.emailBody.set(emailBody);
    }

    public StringProperty emailBodyProperty() {
        return emailBody;
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
