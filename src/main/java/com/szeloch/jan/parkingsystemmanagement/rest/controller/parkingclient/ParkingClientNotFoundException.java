package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingclient;

import lombok.Getter;

@Getter
public class ParkingClientNotFoundException extends RuntimeException {

    private final Long id;

    ParkingClientNotFoundException(final Long id) {
        super("Could not find parking client with id: " + id);
        this.id = id;
    }

}
