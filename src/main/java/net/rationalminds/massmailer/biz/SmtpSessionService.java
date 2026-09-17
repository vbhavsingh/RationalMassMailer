/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.biz;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import net.rationalminds.massmailer.ui.data.MailDetails;
import net.rationalminds.massmailer.utils.Constants;

/**
 *
 * @author Vaibhav Singh
 */
public class SmtpSessionService {

    public static Session getEmailSession(MailDetails details) {
        if (Constants.MAIL_PROVIDER_SMTP2GO.equals(details.getMailProvider())) {
            return buildSmtp2GoSession(details);
        }
        String userName = details.geEmailUserName();
        String mailServer = userName.substring(userName.lastIndexOf("@") + 1);
        if (mailServer.toLowerCase().contains("yahoo.")) {
            return buildYahooSession(details);
        }
        if (mailServer.toLowerCase().contains("gmail.")) {
            return buildGoogleSession(details);
        }
        return null;
    }

    /**
     * The address mail should be sent "From". For SMTP2GO this is distinct
     * from the SMTP login (the account username, e.g. an account/domain
     * name rather than a mailbox); for Gmail/Yahoo the login is itself the
     * mailbox address.
     */
    public static String getFromAddress(MailDetails details) {
        if (Constants.MAIL_PROVIDER_SMTP2GO.equals(details.getMailProvider())) {
            return details.getSmtp2GoFromEmail();
        }
        return details.geEmailUserName();
    }

    /**
     * Who "Send Test Email" should deliver to. For Gmail/Yahoo the From
     * address is the user's own inbox, so sending a test to yourself works.
     * SMTP2GO "From" addresses (e.g. admin@, noreply@) are frequently
     * send-only and do not accept incoming mail, so SMTP2GO requires a
     * separate, real recipient address for the test send.
     */
    public static String getTestRecipient(MailDetails details) {
        if (Constants.MAIL_PROVIDER_SMTP2GO.equals(details.getMailProvider())) {
            return details.getSmtp2GoTestRecipient();
        }
        return details.geEmailUserName();
    }

    private static Session buildGoogleSession(MailDetails details) {
        Properties mailProps = new Properties();

        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.host", "smtp.gmail.com");
        mailProps.put("mail.from", details.geEmailUserName());
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.port", "587");
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.connectiontimeout", "60000");
        mailProps.put("mail.smtp.timeout", "60000");
        mailProps.put("mail.smtp.writetimeout", "60000");

        final PasswordAuthentication usernamePassword;
        usernamePassword = new PasswordAuthentication(details.geEmailUserName(), details.getEmailPassword());

        Authenticator auth = new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return usernamePassword;

            }

        };
        Session session = Session.getInstance(mailProps, auth);
        return session;

    }

    private static Session buildSmtp2GoSession(MailDetails details) {
        Properties mailProps = new Properties();

        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.smtp.host", "mail.smtp2go.com");
        mailProps.put("mail.from", details.getSmtp2GoFromEmail());
        mailProps.put("mail.smtp.starttls.enable", "true");
        String port = details.getSmtp2GoPort();
        mailProps.put("mail.smtp.port", (port == null || port.trim().isEmpty()) ? Constants.SMTP2GO_DEFAULT_PORT : port.trim());
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.connectiontimeout", "60000");
        mailProps.put("mail.smtp.timeout", "60000");
        mailProps.put("mail.smtp.writetimeout", "60000");

        final PasswordAuthentication usernamePassword;
        usernamePassword = new PasswordAuthentication(details.geEmailUserName(), details.getEmailPassword());

        Authenticator auth = new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return usernamePassword;

            }

        };
        Session session = Session.getInstance(mailProps, auth);
        return session;

    }

    private static Session buildYahooSession(MailDetails details) {
        Properties mailProps = new Properties();

        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.smtp.host", "smtp.mail.yahoo.com");
        mailProps.put("mail.from", details.geEmailUserName());
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.port", "587");
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.connectiontimeout", "60000");
        mailProps.put("mail.smtp.timeout", "60000");
        mailProps.put("mail.smtp.writetimeout", "60000");

        final PasswordAuthentication usernamePassword;
        usernamePassword = new PasswordAuthentication(details.geEmailUserName(), details.getEmailPassword());

        Authenticator auth = new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return usernamePassword;

            }

        };
        Session session = Session.getInstance(mailProps, auth);
        return session;

    }

}
