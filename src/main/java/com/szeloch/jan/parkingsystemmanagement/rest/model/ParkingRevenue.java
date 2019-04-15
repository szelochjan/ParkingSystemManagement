package com.szeloch.jan.parkingsystemmanagement.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ParkingRevenue {

    private LocalDate date;
    private Double revenue;

}
