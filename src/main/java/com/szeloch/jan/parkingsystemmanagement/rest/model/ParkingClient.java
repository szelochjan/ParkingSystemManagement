package com.szeloch.jan.parkingsystemmanagement.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ParkingClient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    private String address;

    private ParkingRate.DriverType driverType;

    @OneToOne
    private CreditCard creditCard;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Vehicle> vehicles;

    public ParkingClient(String firstName, String lastName, String address, List<Vehicle> vehicles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.vehicles = vehicles;
    }

    public ParkingClient(String firstName, String lastName, String address, ParkingRate.DriverType driverType, CreditCard creditCard) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.driverType = driverType;
        vehicles = new ArrayList<>();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        vehicle.setOwner(this);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
        vehicle.setOwner(null);
    }

    @Override
    public String toString() {
        return "ParkingClient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}