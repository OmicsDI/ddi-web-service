package uk.ac.ebi.ddi.ws.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND,
        reason = "Database is not available, Please provide correct database.")
public class OmicsDatabaseException extends RuntimeException {
    public OmicsDatabaseException(String message) {
        super(message, null, true, true);
    }

    public OmicsDatabaseException(String message, Throwable cause) {
        super(message, cause, true, true);
    }
}
