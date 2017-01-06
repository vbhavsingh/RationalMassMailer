/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.gmailgreetingssender.biz;

import com.sun.mail.smtp.SMTPMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
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
public class BusinessHelper {

    private static MessageBoard msgBoard = new MessageBoard();

    /**
     *
     * @param file
     * @return
     * @throws BadCsvFileException
     */
    public static DynamicContactsFromCsv getContacts(File file) throws BadCsvFileException {
        msgBoard.appendMessage("Reading contacts file: " + file.getPath());
        List<String[]> recipients = new ArrayList<String[]>();
        String headers[] = null;
        String emailheader = null;
        int headerCount = 0;
        int emailHeaderPos = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i++ == 0) {
                    //fetch the header
                    String headersFrMsg = "Avialable headers: ";
                    headers = line.split(",");
                    for (String header : headers) {
                        if ("email".equalsIgnoreCase(header)) {
                            emailheader = header;
                            emailHeaderPos = headerCount;
                            //  headersFrMsg=header
                        }
                        if (header.contains("@")) {
                            String message = "Bad header! \"@\" symbol found in one header field.";
                            msgBoard.appendMessage(message + " First row must be header.");
                            throw new BadCsvFileException(message);
                        }
                        headersFrMsg += headers.length - 1 == headerCount ? header : header + ", ";
                        headerCount++;
                    }
                    msgBoard.appendMessage(headersFrMsg);
                    if (emailheader == null) {
                        msgBoard.appendMessage("No hedaer with tille \"Email\" found! This is mandatory field. Remember this field is case sensitive.");
                        throw new BadCsvFileException("No hedaer with tille \"Email\" found! This is mandatory field");
                    }
                } else {
                    i++;
                    String cols[] = line.split(",");
                    if (cols.length != headerCount) {
                        msgBoard.appendMessage("Row:" + i + " has unequal number of columns then numbers of headers. All rows and header must have equal number of columns.");
                        throw new BadCsvFileException("Row:" + i + " has unequal number of columns then numbers of headers");
                    }
                    if (!cols[emailHeaderPos].matches(Constants.EMAIL_REGEX_PATERN)) {
                        msgBoard.appendMessage("Row:" + i + " has invalid email address: " + cols[emailHeaderPos] + ". All emails must be in proper format.");
                        throw new BadCsvFileException("Row:" + i + " has invalid email address: " + cols[emailHeaderPos]);
                    }
                    recipients.add(cols);
                }
            }
            br.close();
        } catch (Exception e) {
            throw new BadCsvFileException(e.getMessage());
        }
        DynamicContactsFromCsv contacts = new DynamicContactsFromCsv(headers, recipients);
        contacts.setEmailColumnHeaderName(emailheader);
        contacts.setEmailColumnHeaderPosition(emailHeaderPos);
        return contacts;
    }

    /**
     *
     * @param details
     */
    public static void sendTestEmail(MailDetails details) throws MessagingException, IOException {
        SMTPMessage mail = new SMTPMessage(SmtpSessionService.buildGoogleSession(details));
        msgBoard.appendMessage("Created gmail session with provided credentials to send test email");
        String mailBody = details.getEmailBody();
        String mailSubject = details.getEmailSubject();
        MimeMultipart content = null;

        MimeBodyPart body = new MimeBodyPart();
        body.setText(mailBody, "US-ASCII", "html");

        if (Utilities.isThereImageFileInList(details.getAttachments())) {
            content = new MimeMultipart("related");
        } else {
            content = new MimeMultipart();
        }
        content.addBodyPart(body);
        if (details.getAttachments() != null && details.getAttachments().size() > 0) {
            int j = 0;
            for (File file : details.getAttachments()) {
                if (Utilities.isImageFile(file)) {
                    content.addBodyPart(Utilities.getMimeImagePart(file, j++));
                } else {
                    content.addBodyPart(Utilities.getMimeAttachment(file));
                }
            }
        }

        mail.setSubject(mailSubject);
        InternetAddress to = new InternetAddress(details.getGmailUserName());
        mail.setRecipient(Message.RecipientType.TO, to);
        mail.setContent(content);
        msgBoard.appendMessage("Sending test mail to: " + details.getGmailUserName());
        try {
            Transport.send(mail);
            msgBoard.appendMessage("Test mail is successfully sent to: " + details.getGmailUserName());
        } catch (Exception e) {
            msgBoard.appendMessage("Test mail delievery failed for: " + details.getGmailUserName() + ", because " + e.getMessage());
        }
    }
}
