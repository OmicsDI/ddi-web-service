package uk.ac.ebi.ddi.ws.error.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jose A. Dianes <jdianes@ebi.ac.uk>
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Dataset not found.")
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
