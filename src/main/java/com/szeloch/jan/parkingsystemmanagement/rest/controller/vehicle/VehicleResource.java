package com.szeloch.jan.parkingsystemmanagement.rest.controller.vehicle;

import com.szeloch.jan.parkingsystemmanagement.rest.model.Vehicle;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class VehicleResource extends ResourceSupport {

    private final Vehicle vehicle;

    public VehicleResource(Vehicle vehicle) {
        this.vehicle = vehicle;
        final int id = vehicle.getId();
        add(linkTo(methodOn(VehiclesController.class).findOne(id)).withSelfRel());
        add(linkTo(methodOn(VehiclesController.class).findAll()).withRel("vehicles"));
        add(linkTo(methodOn(VehiclesController.class).findVehicleOwner(id)).withRel("owner"));
        add(linkTo(methodOn(VehiclesController.class).findVehicleParkingTicket(id)).withRel("parking-ticket"));
    }
}
