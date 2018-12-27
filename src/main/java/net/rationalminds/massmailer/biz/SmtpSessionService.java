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

/**
 *
 * @author Vaibhav Singh
 */
public class SmtpSessionService {

    public static Session getEmailSession(MailDetails details) {
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
