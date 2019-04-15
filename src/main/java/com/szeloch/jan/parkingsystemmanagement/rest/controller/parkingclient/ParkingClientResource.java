package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingclient;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingClient;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class ParkingClientResource extends ResourceSupport {

    private final ParkingClient parkingClient;

    public ParkingClientResource(final ParkingClient parkingClient) {
        this.parkingClient = parkingClient;
        final long id = parkingClient.getId();
        add(linkTo(methodOn(ParkingClientsController.class).findOne(id)).withSelfRel());
        add(linkTo(methodOn(ParkingClientsController.class).findAll()).withRel("parking-clients"));
        add(linkTo(methodOn(ParkingClientsController.class).findAllParkingClientVehicles(id)).withRel("parking-client-vehicles"));
    }

}
