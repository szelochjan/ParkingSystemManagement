package com.szeloch.jan.parkingsystemmanagement.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class ParkingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public enum DriverType { REGULAR, DISABLED }

    private DriverType driverType;

    private Integer firstHourRate;

    private Integer secondHourRate;

    private Double thirdAndEachNextHourMultiplier;

    public ParkingRate(DriverType driverType, Integer firstHourRate, Integer secondHourRate, Double thirdAndEachNextHourMultiplier) {
        this.driverType = driverType;
        this.firstHourRate = firstHourRate;
        this.secondHourRate = secondHourRate;
        this.thirdAndEachNextHourMultiplier = thirdAndEachNextHourMultiplier;
    }
}
