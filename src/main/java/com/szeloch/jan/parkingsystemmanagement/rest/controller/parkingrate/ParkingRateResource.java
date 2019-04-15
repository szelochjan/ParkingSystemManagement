package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingrate;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class ParkingRateResource extends ResourceSupport {

    private final ParkingRate parkingRate;

    public ParkingRateResource(final ParkingRate parkingRate) {
        this.parkingRate = parkingRate;
        final int id = parkingRate.getId();
        add(linkTo(methodOn(ParkingRatesController.class).findOne(id)).withSelfRel());
        add(linkTo(methodOn(ParkingRatesController.class).findAll()).withRel("parking-rates"));
    }
}
