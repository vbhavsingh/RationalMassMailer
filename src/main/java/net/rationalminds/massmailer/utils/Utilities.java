/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rationalminds.massmailer.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.DecimalFormat;
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

    private static final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
    private static final String imageTypes[] = {"jpg", "jpeg", "png", "tif", "gif"};
    private static final DecimalFormat timeformat = new DecimalFormat("##.00");

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
    
    public static String nextMailTime(long mils) {
        Date d = new Date(System.currentTimeMillis()+mils);
        return dateFormat.format(d);
    }
    
    public static String elapsedTime(long timeInMs) {
    	String r;
        long time = (int) (timeInMs / 1000);
        long secs = time % 60 > 0 ? time % 60 : 0;
        time = time / 60;
        long mins = time % 60 > 0 ? time % 60 : 0;
        time = time / 60;
        long hours = time % 24 > 0 ? time % 24 : 0;
        long days = time / 24;

        r = String.valueOf(secs);
        String s = secs > 1 ? " seconds " : " second ";
        r = r + s;
        if (mins > 0) {
            String m = (mins > 1) ? " minutes " : " minute ";
            r = mins + m + r;
        }
        if (hours > 0) {
            String h = (hours > 1) ? " hours " : " hour ";
            r = hours + h + r;
        }
        if (days > 0) {
            String d = (days > 1) ? " days " : " day ";
            r = days + d + r;
        }
        return r;
    }

    public static String readTextFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    public static String readClasspathResource(Class<?> anchor, String resourcePath) throws IOException {
        try (InputStream in = anchor.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Copies a bundled classpath resource to a new temp file with the same
     * extension, so code that only knows how to work with {@code File}s
     * (like {@link TemplateBundleLoader#load(File)}) can be reused for a
     * resource shipped inside the jar.
     */
    public static File extractClasspathResourceToTempFile(Class<?> anchor, String resourcePath) throws IOException {
        String suffix = resourcePath.contains(".") ? resourcePath.substring(resourcePath.lastIndexOf('.')) : "";
        File tempFile = File.createTempFile("rationalmassmailer-resource-", suffix);
        tempFile.deleteOnExit();
        try (InputStream in = anchor.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            Files.copy(in, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    public static int nthOccurrence(String text, char c, int occurrence) {
        if (text == null) {
            return 0;
        }
        int thisOccurrence = 0;
        for (int i = 0; i < text.length(); i++) {
            char thisChar = text.charAt(i);
            if (c == thisChar) {
                thisOccurrence++;
                if (thisOccurrence == occurrence) {
                    return i;
                }
            }
        }
        return 0;
    }
}
