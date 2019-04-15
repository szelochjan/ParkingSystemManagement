package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingclient;

import com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket.ParkingTicketNotFoundForVehicleException;
import com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket.ParkingTicketResource;
import com.szeloch.jan.parkingsystemmanagement.rest.controller.vehicle.VehicleNotFoundException;
import com.szeloch.jan.parkingsystemmanagement.rest.controller.vehicle.VehiclesController;
import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingClient;
import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket;
import com.szeloch.jan.parkingsystemmanagement.rest.model.Vehicle;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingClientsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.VehiclesRepository;
import com.szeloch.jan.parkingsystemmanagement.utils.ParkingTicketsUtil;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/parking-clients", produces = "application/hal+json")
public class ParkingClientsController {

    private final ParkingClientsRepository clientsRepository;

    private final VehiclesRepository vehiclesRepository;

    private final ParkingTicketsRepository ticketsRepository;

    private final ParkingTicketsUtil parkingTicketsUtil;

    public ParkingClientsController(final ParkingClientsRepository clientsRepository, final VehiclesRepository vehiclesRepository, final ParkingTicketsRepository ticketsRepository, final ParkingTicketsUtil parkingTicketsUtil) {
        this.clientsRepository = clientsRepository;
        this.vehiclesRepository = vehiclesRepository;
        this.ticketsRepository = ticketsRepository;
        this.parkingTicketsUtil = parkingTicketsUtil;
    }

    @GetMapping
    public Resources<ParkingClientResource> findAll() {

        final List<ParkingClientResource> parkingClients = clientsRepository
                .findAll()
                .stream()
                .map(ParkingClientResource::new)
                .collect(Collectors.toList());

        return new Resources<>(parkingClients,
                linkTo(methodOn(ParkingClientsController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingClientResource> findOne(@PathVariable final long id) {

        return clientsRepository
                .findById(id)
                .map(parkingClient -> ResponseEntity.ok(new ParkingClientResource(parkingClient)))
                .orElseThrow(() -> new ParkingClientNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<ParkingClientResource> newParkingClient(@RequestBody final ParkingClient newParkingClient) {

        ParkingClientResource resource = new ParkingClientResource(clientsRepository.save(newParkingClient));

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingClientResource> updateOrReplaceParkingClient(@RequestBody ParkingClient newParkingClient, @PathVariable final Long id) {

        ParkingClient updatedParkingClient = clientsRepository.findById(id)
                .map(parkingClient -> {
                    parkingClient.setFirstName(newParkingClient.getFirstName());
                    parkingClient.setLastName(newParkingClient.getLastName());
                    parkingClient.setAddress(newParkingClient.getAddress());
                    return clientsRepository.save(parkingClient);
                })
                .orElseGet(() -> {
                    newParkingClient.setId(id);
                    return clientsRepository.save(newParkingClient);
                });

        ParkingClientResource resource = new ParkingClientResource(updatedParkingClient);

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParkingClient(@PathVariable final Long id) {

        return clientsRepository
                .findById(id)
                .map(parkingClient -> {
                    clientsRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ParkingClientNotFoundException(id));
    }

    @GetMapping("/{id}/vehicles")
    public Resources<Resource<Vehicle>> findAllParkingClientVehicles(@PathVariable final Long id) {

        List<Resource<Vehicle>> parkingClientsVehicles = vehiclesRepository.findByOwnerId(id).stream()
                .map(vehicle -> new Resource<>(vehicle,
                        ControllerLinkBuilder.linkTo(methodOn(VehiclesController.class).findOne(vehicle.getId())).withSelfRel(),
                        ControllerLinkBuilder.linkTo(methodOn(VehiclesController.class).findVehicleParkingTicket(vehicle.getId())).withRel("parking-ticket"),
                        ControllerLinkBuilder.linkTo(methodOn(ParkingClientsController.class).findParkingTicketForVehicle(id, vehicle.getId())).withRel("parking-ticket"),
                        ControllerLinkBuilder.linkTo(methodOn(ParkingClientsController.class).activateParkingTicket(id, vehicle.getId())).withRel("activate-parking-ticket"),
                        ControllerLinkBuilder.linkTo(methodOn(ParkingClientsController.class).closeParkingTicket(id, vehicle.getId())).withRel("close-parking-ticket")))
                .collect(Collectors.toList());

        return new Resources<>(parkingClientsVehicles,
                linkTo(methodOn(VehiclesController.class).findAll()).withRel("vehicles"));
    }

    @GetMapping("/{clientId}/vehicles/{vehicle-id}/parking-ticket")
    public ResponseEntity<ParkingTicketResource> findParkingTicketForVehicle(@PathVariable("clientId") Long clientId, @PathVariable("vehicle-id") Integer vehicleId) {

        return Optional.of(getParkingTicketForClientVehicle(getParkingClientById(clientId), vehicleId))
                .map(parkingTicket -> ResponseEntity.ok(new ParkingTicketResource(parkingTicket)))
                .orElseThrow(() -> new ParkingTicketNotFoundForVehicleException("Could not find ticket for vehicle id: ", vehicleId));
    }

    @PutMapping("/{clientId}/vehicles/{vehicle-id}/parking-ticket/activate")
    public ResponseEntity<ResourceSupport> activateParkingTicket(@PathVariable("clientId") final Long clientId, @PathVariable("vehicle-id") final Integer vehicleId) {

        ParkingTicket parkingTicket = (getParkingTicketForClientVehicle(getParkingClientById(clientId), vehicleId));

        if (parkingTicket.getStatus() == ParkingTicket.Status.INACTIVE) {
            parkingTicket.setStartTime(LocalDateTime.now());
            parkingTicket.setStatus(ParkingTicket.Status.ACTIVE);
            return ResponseEntity.ok(new ParkingTicketResource(ticketsRepository.save(parkingTicket)));
        } else return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "You can't activate ticket that is in the " + parkingTicket.getStatus() + " status"));
    }

    @DeleteMapping("/{clientId}/vehicles/{vehicle-id}/parking-ticket/close")
    public ResponseEntity<ResourceSupport> closeParkingTicket(@PathVariable("clientId") final Long clientId, @PathVariable("vehicle-id") final Integer vehicleId) {

        ParkingClient parkingClient = getParkingClientById(clientId);

        ParkingTicket parkingTicket = getParkingTicketForClientVehicle(parkingClient, vehicleId);

        if (parkingTicket.getStatus() == ParkingTicket.Status.ACTIVE) {
            parkingTicket.setEndTime(LocalDateTime.now());
            Double parkingCost = parkingTicketsUtil.calculateParkingTicketCost(parkingTicket, parkingClient.getDriverType());
            parkingTicket.setCost(parkingCost);
            parkingTicket.setStatus(ParkingTicket.Status.CLOSED);
            return ResponseEntity.ok(new ParkingTicketResource(ticketsRepository.save(parkingTicket)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "You can't close ticket that is in the " + parkingTicket.getStatus() + " status"));
    }

    private ParkingClient getParkingClientById(Long clientId) {

        return clientsRepository.findById(clientId)
                .orElseThrow(() -> new ParkingClientNotFoundException(clientId));
    }

    private ParkingTicket getParkingTicketForClientVehicle(ParkingClient parkingClient, Integer vehicleId) {

        Optional<ParkingTicket> parkingTicket = parkingClient.getVehicles()
                .stream()
                .filter(vehicle -> vehicle.getId().equals(vehicleId))
                .findAny()
                .map(Vehicle::getParkingTicket);

        return parkingTicket.orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

}

