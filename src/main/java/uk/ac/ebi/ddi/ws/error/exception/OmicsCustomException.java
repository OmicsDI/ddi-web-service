package uk.ac.ebi.ddi.ws.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * My custom exception class.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND,
        reason = "Either Accession or Database is not available, Please provide correct data.")
public class OmicsCustomException extends RuntimeException {
    public OmicsCustomException(String message) {
        super(message, null, true, true);
    }

    public OmicsCustomException(String message, Throwable cause) {
        super(message, cause, true, true);
    }
}
