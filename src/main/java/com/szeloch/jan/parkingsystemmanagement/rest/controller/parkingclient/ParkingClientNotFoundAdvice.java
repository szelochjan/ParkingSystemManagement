package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingclient;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class ParkingClientNotFoundAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ParkingClientNotFoundException.class)
    public ResponseEntity<VndErrors> parkingClientNotFoundHandler (final ParkingClientNotFoundException ex) {
        return error(ex, HttpStatus.NOT_FOUND, ex.getId().toString());
    }

    private ResponseEntity<VndErrors> error(final Exception ex, final HttpStatus httpStatus, final String logRef) {

        final String message =
                Optional.of(ex.getMessage()).orElse(ex.getClass().getSimpleName());

        return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<VndErrors> assertionException(final IllegalArgumentException e) {
        return error(e, HttpStatus.NOT_FOUND, e.getLocalizedMessage());
    }

}
