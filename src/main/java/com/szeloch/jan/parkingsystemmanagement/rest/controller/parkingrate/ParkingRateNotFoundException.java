package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingrate;

import lombok.Getter;

@Getter
public class ParkingRateNotFoundException extends RuntimeException {

    private final Integer id;

    public ParkingRateNotFoundException(final Integer id) {
        super("Could not find parking rate with id: " + id);
        this.id = id;
    }
}
