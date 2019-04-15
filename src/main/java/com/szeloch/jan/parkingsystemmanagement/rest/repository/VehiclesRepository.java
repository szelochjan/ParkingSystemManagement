package com.szeloch.jan.parkingsystemmanagement.rest.repository;

import com.szeloch.jan.parkingsystemmanagement.rest.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiclesRepository extends JpaRepository<Vehicle, Integer> {

    List<Vehicle> findByOwnerId(Long ownerId);
    Vehicle findByLicensePlate(String licensePlate);

}
