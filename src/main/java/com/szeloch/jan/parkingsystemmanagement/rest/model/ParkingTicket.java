package com.szeloch.jan.parkingsystemmanagement.rest.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class ParkingTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public enum Status { INACTIVE, ACTIVE, CLOSED }

    private Double cost;

    private Boolean isPaid;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Status status;

    @OneToOne
    @JsonManagedReference
    private Vehicle parkedVehicle;

    public ParkingTicket(Vehicle parkedVehicle) {
        cost = 0.0;
        isPaid = false;
        startTime = null;
        endTime = null;
        status = Status.INACTIVE;
        this.parkedVehicle = parkedVehicle;
    }

}
