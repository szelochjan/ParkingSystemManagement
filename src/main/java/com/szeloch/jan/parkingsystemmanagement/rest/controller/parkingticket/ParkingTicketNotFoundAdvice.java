package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class ParkingTicketNotFoundAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ParkingTicketNotFoundException.class)
    public ResponseEntity<VndErrors> parkingTicketNotFoundHandler (final ParkingTicketNotFoundException ex) {
        return error(ex, HttpStatus.NOT_FOUND, ex.getId().toString());
    }

    @ExceptionHandler(ParkingTicketNotFoundForVehicleException.class)
    public ResponseEntity<VndErrors> parkingTicketNotFoundForVehicleHandler (final ParkingTicketNotFoundForVehicleException ex) {
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
