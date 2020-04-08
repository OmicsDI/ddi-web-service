package uk.ac.ebi.ddi.ws.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.ac.ebi.ddi.ws.modules.dataset.controller.DatasetController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(assignableTypes = {DatasetController.class})
public class OmicsGlobalCustomExceptionHandler extends ResponseEntityExceptionHandler {

    // Let Spring BasicErrorController handle the exception, we just override the status code
    @ExceptionHandler(OmicsCustomException.class)
    public void springHandleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "Either Accession or Database is not available");
    }
}
