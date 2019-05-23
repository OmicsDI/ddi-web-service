package uk.ac.ebi.ddi.ws.util;

import org.apache.commons.io.FilenameUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    private static Pattern fileNamePattern = Pattern.compile("filename=([^&]*)");

    public static Optional<String> getFileExtension(final String url) {

        Objects.requireNonNull(url, "url is null");

        if (url.contains(".")) {

            final String sub = url.substring(url.lastIndexOf('.') + 1);

            if (sub.length() == 0) {
                return Optional.empty();
            }

            if (sub.contains("?")) {
                return Optional.of(sub.substring(0, sub.indexOf('?')));
            }

            return Optional.of(sub);
        }

        return Optional.empty();
    }

    public static String getFilenameFromUrl(final String url) {
        Matcher matcher = fileNamePattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return FilenameUtils.getName(url);
    }
}
