package com.szeloch.jan.parkingsystemmanagement.rest.controller.vehicle;

import lombok.Getter;

@Getter
public class VehicleNotFoundException extends RuntimeException {

    private final Integer id;
    private final String licensePlate;

    public VehicleNotFoundException(final Integer id) {
        super("Could not find vehicle with id: " + id);
        this.id = id;
        licensePlate = "";
    }

    public VehicleNotFoundException(final String licensePlate) {
        super("Could not find vehicle with licensePlate: " + licensePlate);
        this.licensePlate = licensePlate;
        this.id = null;
    }

}
