/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

/**
 *
 * @author Vaibhav Singh
 */
public class Utilities {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    private static final String imageTypes[] = {"jpg", "jpeg", "png", "tif", "gif"};

    public static String getOpenDialogInitialDir() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        String home = System.getProperty("user.home");
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            return home + File.separator + "Desktop";
        } else if (OS.indexOf("win") >= 0) {
            return home + File.separator + "Desktop";
        }
        return home;
    }

    public static boolean isImageFile(File file) {
        String type = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        type = type.toLowerCase();
        if (Arrays.asList(imageTypes).contains(type)) {
            return true;
        }
        return false;
    }

    public static boolean isThereImageFileInList(List<File> files) {
        if (files == null) {
            return false;
        }
        for (File file : files) {
            if (isImageFile(file)) {
                return true;
            }
        }
        return false;
    }

    public static MimeBodyPart getMimeImagePart(File file, int contentID) throws IOException, MessagingException {
        String fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        MimeBodyPart image = new MimeBodyPart();
        image.attachFile(file);
        image.setContentID("<" + contentID + ">");
        image.setDisposition(MimeBodyPart.INLINE);
        image.setFileName(file.getName());
        image.addHeader("Content-Type", "image/" + fileType);
        return image;
    }

    public static MimeBodyPart getMimeAttachment(File file) throws IOException, MessagingException {
        MimeBodyPart attachment = new MimeBodyPart();
        attachment.attachFile(file);
        attachment.setFileName(file.getName());
        return attachment;
    }

    public static String currentTimestamp() {
        return dateFormat.format(new Date()) + " : ";
    }

}
