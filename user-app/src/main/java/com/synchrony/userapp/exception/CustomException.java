package com.synchrony.userapp.exception;

import com.synchrony.userapp.model.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomException extends ResponseEntityExceptionHandler {
    /**
     * Exception to be thrown when validation on an argument annotated with
     * {@code @Valid} fails.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return error message
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + " " + error.getDefaultMessage());
        }
        Response error = new Response(details.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<Object> handleExceptions(RecordAlreadyExistsException exception) {
        Response response = new Response();
        response.setMessage(exception.getMessage());
        ResponseEntity<Object> entity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        return entity;
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Object> handleExceptions(RecordNotFoundException exception) {
        Response response = new Response();
        response.setMessage(exception.getMessage());
        ResponseEntity<Object> entity = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        return entity;
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public  ResponseEntity<Object> handleImageNotFoundException(
            ImageNotFoundException ex) {
        Response response = new Response();
        response.setMessage(ex.getMessage());
        ResponseEntity<Object> entity = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        return entity;
    }

}
