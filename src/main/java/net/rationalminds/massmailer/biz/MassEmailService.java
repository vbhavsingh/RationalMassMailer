/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.biz;

import com.sun.mail.smtp.SMTPMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import net.rationalminds.massmailer.ui.data.MailDetails;
import net.rationalminds.massmailer.ui.data.MessageBoard;
import net.rationalminds.massmailer.utils.Utilities;

/**
 *
 * @author Vaibhav Singh
 */
public class MassEmailService implements Runnable {

    private static final MessageBoard msgBoard = new MessageBoard();

    private final MailDetails details;

    public MassEmailService(MailDetails details) {
        this.details = details;
    }

    @Override
    public void run() {
        try {
            sendMassMails();
        } catch (Exception ex) {
            msgBoard.appendMessage("error occured while sending emails: " + ex.getMessage());
        }
    }

    /**
     *
     * @param details
     * @throws MessagingException
     */
    private void sendMassMails() throws MessagingException, IOException {
        String mailBody = details.getEmailBody();
        String mailSubject = details.getEmailSubject();
        List<String[]> contacts = details.getContacts().getRecepients();
        String headers[] = details.getContacts().getHeaders();
        MimeMultipart content = null;

        SMTPMessage mail = new SMTPMessage(SmtpSessionService.getEmailSession(details));
        msgBoard.appendMessage("Created email session with provided credentials.");
        int count = 1;
        for (String[] contact : contacts) {
            String indvMailBody = mailBody;
            String indvMailSubject = mailSubject;
            int i = 0;
            for (String header : headers) {
                indvMailSubject = indvMailSubject.replace("{{" + header + "}}", contact[i]);
                indvMailBody = indvMailBody.replace("{{" + header + "}}", contact[i]);
                i++;
            }

            MimeBodyPart body = new MimeBodyPart();
            body.setText(indvMailBody, "US-ASCII", "html");

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

            mail.setSubject(indvMailSubject);
            InternetAddress to = new InternetAddress(contact[details.getContacts().getEmailColumnHeaderPosition()]);
            mail.setRecipient(Message.RecipientType.TO, to);
            mail.setContent(content);
            String contactName = contact[details.getContacts().getEmailColumnHeaderPosition()];
            msgBoard.appendMessage("Sending mail to: " + contactName + ", mailing to " + count + " contact out of " + contacts.size());
            System.out.println("Sending mail to: " + contactName + ", mailing to " + count + " contact out of " + contacts.size());
            try {
                Transport.send(mail);
                msgBoard.appendMessage("Mail is successfully sent to: " + contact[details.getContacts().getEmailColumnHeaderPosition()]);
            } catch (Exception e) {
                msgBoard.appendMessage("Mail delivery failed for: " + contact[details.getContacts().getEmailColumnHeaderPosition()] + ", because " + e.getMessage());
            } finally {
                count++;
            }
        }
    }

}
