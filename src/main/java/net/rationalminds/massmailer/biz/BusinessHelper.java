/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.biz;

import com.sun.mail.smtp.SMTPMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
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
public class BusinessHelper {

    private static MessageBoard msgBoard = MessageBoard.getMessageBoard();
    
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     *
     * @param file
     * @return
     * @throws BadCsvFileException
     */
    public static DynamicContactsFromCsv getContacts(File file) throws BadCsvFileException {
        msgBoard.appendMessage("Reading contacts file: " + file.getPath());
        List<String[]> recipients = new ArrayList<String[]>();
        List<String> badRecords = new ArrayList<>();
        
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
                    String headersFrMsg = "Available headers: ";
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
                            LOGGER.log(Level.SEVERE, message + " First row must be header.");
                            throw new BadCsvFileException(message);
                        }
                        headersFrMsg += headers.length - 1 == headerCount ? header : header + ", ";
                        headerCount++;
                    }
                    msgBoard.appendMessage(headersFrMsg);
                    if (emailheader == null) {
                    	String message = "No hedaer with tille \"Email\" found! This is mandatory field. Remember this field is case sensitive.";
                        msgBoard.appendMessage(message);
                        LOGGER.log(Level.SEVERE, message );
                        throw new BadCsvFileException("No hedaer with tille \"Email\" found! This is mandatory field");
                    }
                } else {
                    i++;
                    String cols[] = line.split(",");
                    if (cols.length != headerCount) {
                    	String message = "Row:" + i + " has unequal number of columns then numbers of headers. Rows and header must have equal number of columns. Neglecting this contact.";
                        msgBoard.appendMessage(message);
                        LOGGER.log(Level.SEVERE, message );
                        continue;
                    }
                    if (!cols[emailHeaderPos].matches(Constants.EMAIL_REGEX_PATERN)) {
                    	String message = "Row:" + i + " has invalid email address: " + cols[emailHeaderPos] + ". Email address must be in proper format. Neglecting this contact.";
                        msgBoard.appendMessage(message);
                        LOGGER.log(Level.SEVERE, message );
                        continue;
                    }
                    recipients.add(cols);
                }
            }
            br.close();
        } catch (Exception e) {
            throw new BadCsvFileException(e.getMessage());
        }
        DynamicContactsFromCsv contacts = new DynamicContactsFromCsv(headers, recipients);
        LOGGER.info(recipients.size()+", valid contacts found");
        msgBoard.appendMessage(recipients.size()+", valid contacts found");
        contacts.setEmailColumnHeaderName(emailheader);
        contacts.setEmailColumnHeaderPosition(emailHeaderPos);
        return contacts;
    }

    /**
     *
     * @param details
     */
    public static void sendTestEmail(MailDetails details) throws MessagingException, IOException {
        SMTPMessage mail = new SMTPMessage(SmtpSessionService.getEmailSession(details));
        msgBoard.appendMessage("Created email session with provided credentials to send test email");
        LOGGER.info("Created email session with provided credentials to send test email");
        /*String mailBody = details.getEmailBody();*/
        String mailBody = details.getHtmlEmailBody();
        String mailSubject = details.getEmailSubject();
        MimeMultipart content = null;

        MimeBodyPart body = new MimeBodyPart();
        body.setText(mailBody, "US-ASCII", "html");
        LOGGER.info("mail body type : html, encoding : US-ASCII");

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
        InternetAddress to = new InternetAddress(details.geEmailUserName());
        mail.setRecipient(Message.RecipientType.TO, to);
        mail.setContent(content);
        msgBoard.appendMessage("Sending test mail to: " + details.geEmailUserName());
        LOGGER.info("Sending test mail to: " + details.geEmailUserName());
        Transport.send(mail);
        msgBoard.appendMessage("Test mail is successfully sent to: " + details.geEmailUserName());
        LOGGER.info("\"Test mail is successfully sent to: \" + details.geEmailUserName()");
    }
}
