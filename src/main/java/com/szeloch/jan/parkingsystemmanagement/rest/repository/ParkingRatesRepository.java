package com.szeloch.jan.parkingsystemmanagement.rest.repository;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRatesRepository extends JpaRepository<ParkingRate, Integer> {

    ParkingRate findByDriverType(ParkingRate.DriverType driverType);

}
