package com.szeloch.jan.parkingsystemmanagement.rest.controller.vehicle;

import com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingclient.ParkingClientResource;
import com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket.ParkingTicketResource;
import com.szeloch.jan.parkingsystemmanagement.rest.model.Vehicle;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingClientsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.VehiclesRepository;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
@RequestMapping(value = "/vehicles", produces = "application/hal+json")
public class VehiclesController {

    private final VehiclesRepository vehiclesRepository;

    public VehiclesController(VehiclesRepository vehiclesRepository) {
        this.vehiclesRepository = vehiclesRepository;
    }

    @GetMapping
    public Resources<VehicleResource> findAll() {

        List<VehicleResource> vehicles = vehiclesRepository.findAll().stream()
                .map(VehicleResource::new)
                .collect(Collectors.toList());

        return new Resources<>(vehicles,
                linkTo(methodOn(VehiclesController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable final Integer id) {

        return vehiclesRepository.findById(id)
                .map(VehicleResource::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<?> newVehicle(@RequestBody final Vehicle newVehicle) {

        VehicleResource resource = new VehicleResource(vehiclesRepository.save(newVehicle));

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResource> updateOrReplaceVehicle(@RequestBody final Vehicle newVehicle, @PathVariable final Integer id) {

        Vehicle updatedVehicle = vehiclesRepository.findById(id)
                .map(vehicle -> {
                    vehicle.setOwner(newVehicle.getOwner());
                    vehicle.setLicensePlate(newVehicle.getLicensePlate());
                    vehicle.setCompanyName(newVehicle.getCompanyName());
                    vehicle.setModelName(newVehicle.getModelName());
                    return vehiclesRepository.save(vehicle);
                })
                .orElseGet(() -> {
                    newVehicle.setId(id);
                    return vehiclesRepository.save(newVehicle);
                });

        VehicleResource resource = new VehicleResource(updatedVehicle);

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable final Integer id) {

        return vehiclesRepository
                .findById(id)
                .map(vehicle -> {
                    vehiclesRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @GetMapping("/{id}/owner")
    public ResponseEntity<ParkingClientResource> findVehicleOwner(@PathVariable final Integer id) {

        return vehiclesRepository.findById(id)
                .map(vehicle -> new ParkingClientResource(vehicle.getOwner()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @GetMapping("/{id}/parking-ticket")
    public ResponseEntity<ParkingTicketResource> findVehicleParkingTicket(@PathVariable final Integer id) {

        return vehiclesRepository.findById(id)
                .map(Vehicle::getParkingTicket)
                .map(ParkingTicketResource::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @GetMapping("/license-plate")
    public ResponseEntity<Vehicle> findVehicleByLicensePlate(@RequestParam("licensePlate") final String licensePlate) {

        return Optional.of(vehiclesRepository.findByLicensePlate(licensePlate))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new VehicleNotFoundException(licensePlate));
    }

}
