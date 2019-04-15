package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket;

import lombok.Getter;

@Getter
public class ParkingTicketNotFoundException extends RuntimeException {

    private final Long id;

    public ParkingTicketNotFoundException(final Long id) {
        super("Could not find parking ticket with id: " + id);
        this.id = id;
    }

}
