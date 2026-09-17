package net.rationalminds.massmailer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import java.util.zip.GZIPInputStream;

/**
 * Loads a mail body template that may be a plain .txt/.html file, or a
 * .zip/.tar/.tar.gz/.tgz bundle containing one HTML/text file plus the
 * images (and other attachments) it references by filename. Any
 * {@code <img src="filename">} tag whose filename matches a bundled image
 * is rewritten to {@code cid:N} to match the inline-image numbering
 * {@code MassEmailService}/{@code BusinessHelper} assign to attachments.
 */
public class TemplateBundleLoader {

    private static final Pattern IMG_SRC_PATTERN =
            Pattern.compile("(<img\\b[^>]*\\bsrc\\s*=\\s*[\"'])([^\"']+)([\"'][^>]*>)", Pattern.CASE_INSENSITIVE);

    public static class TemplateBundle {
        public final String html;
        public final List<File> attachments;

        TemplateBundle(String html, List<File> attachments) {
            this.html = html;
            this.attachments = attachments;
        }
    }

    public static TemplateBundle load(File source) throws IOException {
        String name = source.getName().toLowerCase(Locale.ENGLISH);
        if (name.endsWith(".zip")) {
            return loadZip(source);
        }
        if (name.endsWith(".tar.gz") || name.endsWith(".tgz")) {
            return loadTar(source, true);
        }
        if (name.endsWith(".tar")) {
            return loadTar(source, false);
        }
        return new TemplateBundle(Utilities.readTextFile(source), new ArrayList<>());
    }

    private static TemplateBundle loadZip(File source) throws IOException {
        LinkedHashMap<String, byte[]> entries = new LinkedHashMap<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(source.toPath()))) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    entries.put(entry.getName(), zin.readAllBytes());
                }
            }
        }
        return assemble(entries, source.getName());
    }

    private static TemplateBundle loadTar(File source, boolean gzipped) throws IOException {
        LinkedHashMap<String, byte[]> entries = new LinkedHashMap<>();
        InputStream rawIn = Files.newInputStream(source.toPath());
        InputStream maybeGzipped = gzipped ? new GZIPInputStream(rawIn) : rawIn;
        try (TarArchiveInputStream tin = new TarArchiveInputStream(maybeGzipped)) {
            TarArchiveEntry entry;
            while ((entry = tin.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    entries.put(entry.getName(), tin.readAllBytes());
                }
            }
        }
        return assemble(entries, source.getName());
    }

    /**
     * Given the raw archive entries (in archive order), finds the HTML/text
     * body, extracts every other entry to a temp file, rewrites {@code <img>}
     * references to the matching {@code cid:N}, and returns the finished
     * bundle with attachments ordered so referenced images come first, in
     * the order they first appear in the HTML - matching the cid numbering
     * MassEmailService/BusinessHelper assign when building the message.
     */
    private static TemplateBundle assemble(LinkedHashMap<String, byte[]> entries, String archiveName) throws IOException {
        String htmlEntryName = null;
        String txtEntryName = null;
        for (String entryName : entries.keySet()) {
            if (isNoise(entryName)) {
                continue;
            }
            String lower = entryName.toLowerCase(Locale.ENGLISH);
            if (htmlEntryName == null && (lower.endsWith(".html") || lower.endsWith(".htm"))) {
                htmlEntryName = entryName;
            } else if (txtEntryName == null && lower.endsWith(".txt")) {
                txtEntryName = entryName;
            }
        }
        String bodyEntryName = htmlEntryName != null ? htmlEntryName : txtEntryName;
        if (bodyEntryName == null) {
            throw new IOException("No .html or .txt template file found inside " + archiveName);
        }
        String html = new String(entries.get(bodyEntryName), StandardCharsets.UTF_8);

        File tempDir = Files.createTempDirectory("rationalmassmailer-template-").toFile();
        tempDir.deleteOnExit();

        LinkedHashMap<String, File> extractedByBasename = new LinkedHashMap<>();
        List<File> nonImageAttachments = new ArrayList<>();
        for (var e : entries.entrySet()) {
            String entryName = e.getKey();
            if (entryName.equals(bodyEntryName) || isNoise(entryName)) {
                continue;
            }
            String basename = basename(entryName);
            File extracted = new File(tempDir, basename);
            try (FileOutputStream fos = new FileOutputStream(extracted)) {
                fos.write(e.getValue());
            }
            extracted.deleteOnExit();
            if (Utilities.isImageFile(extracted)) {
                extractedByBasename.put(basename.toLowerCase(Locale.ENGLISH), extracted);
            } else {
                nonImageAttachments.add(extracted);
            }
        }

        List<File> orderedImages = new ArrayList<>();
        Matcher matcher = IMG_SRC_PATTERN.matcher(html);
        StringBuilder rewritten = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            String src = matcher.group(2);
            File matched = resolveImageReference(src, extractedByBasename);
            rewritten.append(html, lastEnd, matcher.start());
            if (matched != null) {
                int cid = orderedImages.indexOf(matched);
                if (cid < 0) {
                    cid = orderedImages.size();
                    orderedImages.add(matched);
                }
                rewritten.append(matcher.group(1)).append("cid:").append(cid).append(matcher.group(3));
            } else {
                rewritten.append(matcher.group());
            }
            lastEnd = matcher.end();
        }
        rewritten.append(html.substring(lastEnd));

        for (File image : extractedByBasename.values()) {
            if (!orderedImages.contains(image)) {
                orderedImages.add(image);
            }
        }

        List<File> attachments = new ArrayList<>(orderedImages);
        attachments.addAll(nonImageAttachments);
        return new TemplateBundle(rewritten.toString(), attachments);
    }

    private static File resolveImageReference(String src, LinkedHashMap<String, File> extractedByBasename) {
        String lower = src.toLowerCase(Locale.ENGLISH);
        if (lower.startsWith("http://") || lower.startsWith("https://")
                || lower.startsWith("data:") || lower.startsWith("cid:")) {
            return null;
        }
        return extractedByBasename.get(basename(src).toLowerCase(Locale.ENGLISH));
    }

    private static String basename(String entryPath) {
        String normalized = entryPath.replace('\\', '/');
        int lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }

    private static boolean isNoise(String entryName) {
        String basename = basename(entryName);
        return entryName.startsWith("__MACOSX/") || basename.equals(".DS_Store")
                || basename.startsWith("._") || basename.isEmpty();
    }
}
