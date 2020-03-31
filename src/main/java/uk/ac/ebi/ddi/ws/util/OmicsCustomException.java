package uk.ac.ebi.ddi.ws.util;

/**
 * My custom exception class.
 */
public class OmicsCustomException extends Exception {
    public OmicsCustomException(String message) {
        super(message, null, false, false);
    }
}
