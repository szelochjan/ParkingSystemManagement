package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingrate;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingRatesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.Resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@RestController
@RequestMapping(value = "/parking-rates", produces = "application/hal+json")
public class ParkingRatesController {

    private final ParkingRatesRepository repository;

    public ParkingRatesController(ParkingRatesRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Resources<ParkingRateResource> findAll() {

        List<ParkingRateResource> parkingRates = repository.findAll().stream()
                .map(ParkingRateResource::new)
                .collect(Collectors.toList());

        return new Resources<>(parkingRates,
                linkTo(methodOn(ParkingRatesController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingRateResource> findOne(@PathVariable final Integer id) {

        return repository
                .findById(id)
                .map(parkingRate -> ResponseEntity.ok(new ParkingRateResource(parkingRate)))
                .orElseThrow(() -> new ParkingRateNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<ParkingRateResource> newParkingRate(@RequestBody final ParkingRate newParkingRate) {

        ParkingRateResource resource = new ParkingRateResource(repository.save(newParkingRate));

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingRateResource> updateOrReplaceParkingRate(@RequestBody final ParkingRate newParkingRate, @PathVariable final Integer id) {

        ParkingRate updatedParkingRate = repository.findById(id)
                .map(parkingRate -> {
                    parkingRate.setDriverType(newParkingRate.getDriverType());
                    parkingRate.setFirstHourRate(newParkingRate.getFirstHourRate());
                    parkingRate.setSecondHourRate(newParkingRate.getSecondHourRate());
                    parkingRate.setThirdAndEachNextHourMultiplier(newParkingRate.getThirdAndEachNextHourMultiplier());
                    return repository.save(parkingRate);
                })
                .orElseGet(() -> {
                    newParkingRate.setId(id);
                    return repository.save(newParkingRate);
                });

        ParkingRateResource resource = new ParkingRateResource(updatedParkingRate);

        return ResponseEntity
                .created(URI.create(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParkingRate(@PathVariable final Integer id) {

        return repository
                .findById(id)
                .map(parkingRate -> {
                    repository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ParkingRateNotFoundException(id));
    }

}
