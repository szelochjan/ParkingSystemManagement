package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class ParkingTicketResource extends ResourceSupport {

    private final ParkingTicket parkingTicket;

    public ParkingTicketResource(ParkingTicket parkingTicket) {
        this.parkingTicket = parkingTicket;
        final long id = parkingTicket.getId();
        add(linkTo(methodOn(ParkingTicketsController.class).findOne(id)).withSelfRel());
        add(linkTo(methodOn(ParkingTicketsController.class).findAll()).withRel("parking-tickets"));
    }
}
