package com.szeloch.jan.parkingsystemmanagement.rest.repository;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingClientsRepository extends JpaRepository<ParkingClient, Long> {}
