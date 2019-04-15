package com.szeloch.jan.parkingsystemmanagement.rest.repository;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingTicketsRepository extends JpaRepository<ParkingTicket, Long> {

    @Query("select t from ParkingTicket t where t.endTime > :timeFrom and t.endTime < :timeTo and t.status = 2")
    List<ParkingTicket> findAllByEndTimeBetween(@Param("timeFrom") LocalDateTime timeFrom, @Param("timeTo") LocalDateTime timeTo);

}
