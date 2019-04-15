package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket;

import lombok.Getter;

@Getter
public class ParkingTicketNotFoundForVehicleException extends RuntimeException {

    private final Integer id;

    public ParkingTicketNotFoundForVehicleException(String message, Integer id) {
        super(message + id);
        this.id = id;
    }

}
