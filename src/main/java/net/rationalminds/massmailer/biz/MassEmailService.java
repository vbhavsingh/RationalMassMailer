/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.biz;

import com.sun.mail.smtp.SMTPMessage;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

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
	
	private static final Logger LOG= Logger.getLogger(MassEmailService.class.getName());

    private static MessageBoard msgBoard = MessageBoard.getMessageBoard();
    
    private StringProperty mainScreenLiveMessageProperty ;
    
    Text mainScreenLiveMessage;
   
    
    private static boolean PAUSE_MAILS=false;

    private final MailDetails details;
    
    private static long previousSleepTimer =0;

    public MassEmailService(MailDetails details,StringProperty mainScreenLiveMessageProperty,Text mainScreenLiveMessage) {
        this.details = details;
        this.mainScreenLiveMessageProperty = mainScreenLiveMessageProperty;
        this.mainScreenLiveMessage = mainScreenLiveMessage;
    }
    
    public static void pauseMailSend() {
    	PAUSE_MAILS = true;
    }
    
    public static void resumeMailSend() {
    	PAUSE_MAILS = false;
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
        /*String mailBody = details.getEmailBody();*/
    	String mailBody = details.getHtmlEmailBody();
        String mailSubject = details.getEmailSubject();
        List<String[]> contacts = details.getContacts().getRecepients();
        String headers[] = details.getContacts().getHeaders();
        MimeMultipart content = null;
        
        SMTPMessage mail = new SMTPMessage(SmtpSessionService.getEmailSession(details));
        msgBoard.appendMessage("Created email session with provided credentials.");
        int count = 1;
        int failed = 0;
        int passed = 0;

        for (String[] contact : contacts) {
        	long sleepTimer = getSleepTimer();
        	long lastInitSendTime = System.currentTimeMillis();
        	/* check for pause flag and start a conditional loop */
        	if(count > 1) {
        		pauseSensor();
        	}
        	long timeElapsedInPause = System.currentTimeMillis() - lastInitSendTime;
        	/* reduce the pause time from sleep and if pause time is large, set sleeptimer as 0 to send next mail immediately*/
        	sleepTimer = sleepTimer - timeElapsedInPause;
        	sleepTimer = sleepTimer <0?0:sleepTimer;
        	
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
            msgBoard.appendMessage("Sending mail to: " + contactName + ". " + count + " out of " + contacts.size() + " contacts tried.");
            synchronized (mainScreenLiveMessageProperty) {
            	mainScreenLiveMessage.setStyle("");
            	mainScreenLiveMessage.setStyle("-fx-fill: blue;");
            	mainScreenLiveMessageProperty.set("Sending mail to: " + contactName+". "+count + "/" + contacts.size());	      	
			}
            try {
                Transport.send(mail);
                msgBoard.appendMessage("Mail is successfully sent to: " + contact[details.getContacts().getEmailColumnHeaderPosition()]);
                passed++;
                synchronized (mainScreenLiveMessageProperty) {
                	mainScreenLiveMessage.setStyle("");
                	mainScreenLiveMessage.setStyle("-fx-fill: green;");
                	mainScreenLiveMessageProperty.set("Sent mail to: " + contactName+". "+count + "/" + contacts.size());
				}
            } catch (Exception e) {
                msgBoard.appendMessage("Mail delivery failed for: " + contact[details.getContacts().getEmailColumnHeaderPosition()] + ", because " + e.getMessage());
                failed++;
                synchronized (mainScreenLiveMessageProperty) {
                	mainScreenLiveMessage.setStyle("");
                	mainScreenLiveMessage.setStyle("-fx-fill: red;");
            		mainScreenLiveMessageProperty.set("Mail to: " + contactName+" failed. "+count + "/" + contacts.size());
				}
            } finally {
                count++;
            }
            try {
            	msgBoard.appendMessage("Next mail to be send at " + Utilities.nextMailTime(sleepTimer));
                Thread.sleep(sleepTimer);
            }catch(Exception e) {
            	msgBoard.appendMessage("Some fatal issue occured, can't proceed. Please close the program.");
            	break;
            }
        }
        mainScreenLiveMessage.setStyle("");
    	mainScreenLiveMessage.setStyle("-fx-fill: green;");
		mainScreenLiveMessageProperty.set(passed + " emails succesfully send, " + failed + " deliveries failed");
        msgBoard.appendMessage(passed + " emails succesfully send, " + failed + " deliveries failed");
    }
    
    /**
     * Loop till PAUSE_MAILS is FALSE
     */
    private void pauseSensor() {
    	try {
    		long startTime = System.currentTimeMillis();
        	while(true) {
        		if(PAUSE_MAILS) {
        			 synchronized (mainScreenLiveMessageProperty) {
        				long elapsedTime = System.currentTimeMillis() - startTime;
                     	mainScreenLiveMessage.setStyle("");
                     	mainScreenLiveMessage.setStyle("-fx-fill: red;");
                 		mainScreenLiveMessageProperty.set("Paused since "+Utilities.elapsedTime(elapsedTime));
     				}
        			Thread.sleep(1000);
        			continue;
        		}else {
        			break;
        		}
        	}
        	}catch(Exception e){
        		msgBoard.appendMessage("Some fatal issue occured in pasuse/ resume. Can't proceed. Please close the program.");
        	}
    }
    
    /**
     * Get SLeep timer
     * @return
     */
    private long getSleepTimer() {
    	long sleepTimer = Long.parseLong(details.getMailsPerHour());
        
        if(sleepTimer <=0 || sleepTimer >999) {
        	msgBoard.appendMessage("Bad mails per hour value specified. Using default 60 mails per hour throttling.");
        	sleepTimer =60;
        }else {
        	if(sleepTimer != previousSleepTimer) {
        		msgBoard.appendMessage("Mail send rate is at "+sleepTimer +" mails per hour");
        	}
        	
        }
        
        sleepTimer = (60*60*1000)/sleepTimer;
        previousSleepTimer = sleepTimer;
        return sleepTimer;	
    }

}
