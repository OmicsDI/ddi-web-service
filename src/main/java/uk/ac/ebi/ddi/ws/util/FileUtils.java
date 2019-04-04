package uk.ac.ebi.ddi.ws.util;

import java.util.Objects;
import java.util.Optional;

public class FileUtils {

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
}
