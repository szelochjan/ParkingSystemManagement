package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRevenue;
import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import com.szeloch.jan.parkingsystemmanagement.utils.ParkingTicketsUtil;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket.Status.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
@RequestMapping(value = "/parking-tickets", produces = "application/hal+json")
public class ParkingTicketsController {

    private final ParkingTicketsRepository repository;

    private final ParkingTicketsUtil parkingTicketsUtil;

    public ParkingTicketsController(ParkingTicketsRepository repository, ParkingTicketsUtil parkingTicketsUtil) {
        this.repository = repository;
        this.parkingTicketsUtil = parkingTicketsUtil;
    }

    @GetMapping
    public Resources<ParkingTicketResource> findAll() {

        List<ParkingTicketResource> parkingTickets = repository.findAll().stream()
                .map(ParkingTicketResource::new)
                .collect(Collectors.toList());

        return new Resources<>(parkingTickets,
                linkTo(methodOn(ParkingTicketsController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingTicketResource> findOne(@PathVariable final Long id) {

        return repository.findById(id)
                .map(ParkingTicketResource::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ParkingTicketNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<ParkingTicketResource> newParkingTicket(@RequestBody final ParkingTicket newParkingTicket) {

        newParkingTicket.setStatus(INACTIVE);
        ParkingTicketResource resource = new ParkingTicketResource(repository.save(newParkingTicket));

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingTicketResource> updateOrReplaceParkingTicket(@RequestBody final ParkingTicket newParkingTicket, @PathVariable final Long id) {

        ParkingTicket updatedParkingTicket = repository.findById(id)
                .map(parkingTicket -> {
                    parkingTicket.setStartTime(newParkingTicket.getStartTime());
                    parkingTicket.setEndTime(newParkingTicket.getEndTime());
                    parkingTicket.setCost(newParkingTicket.getCost());
                    parkingTicket.setIsPaid(newParkingTicket.getIsPaid());
                    return repository.save(parkingTicket);
                })
                .orElseGet(() -> {
                    newParkingTicket.setId(id);
                    return repository.save(newParkingTicket);
                });

        ParkingTicketResource resource = new ParkingTicketResource(updatedParkingTicket);

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParkingTicket(@PathVariable final Long id) {

        return repository
                .findById(id)
                .map(parkingTicket -> {
                    repository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ParkingTicketNotFoundException(id));
    }

    @GetMapping("/revenue/{year}/{month}/{day}")
    public ParkingRevenue getCost(@PathVariable final Integer year, @PathVariable final Integer month,
                                  @PathVariable final Integer day, @RequestParam final Boolean paidOnly) {

        return parkingTicketsUtil.getRevenueForGivenDay(LocalDate.of(year, month, day), paidOnly);
    }


}
