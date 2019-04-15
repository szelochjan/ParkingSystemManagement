package com.szeloch.jan.parkingsystemmanagement.rest.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String licensePlate;

    private String companyName;

    private String modelName;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private ParkingTicket parkingTicket;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parkingclient_id")
    @JsonIgnore
    private ParkingClient owner;

    public Vehicle(String licensePlate, String companyName, String modelName, ParkingClient owner) {
        this.licensePlate = licensePlate;
        this.companyName = companyName;
        this.modelName = modelName;
        this.owner = owner;
    }

    public Vehicle(String licensePlate, String companyName, String modelName) {
        this.licensePlate = licensePlate;
        this.companyName = companyName;
        this.modelName = modelName;
        this.parkingTicket = new ParkingTicket(this);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", companyName='" + companyName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", ownerName=" + owner.getFullName() + '\'' +
                ", ownerId=" + owner.getId() + '\'' +
                '}';
    }
}
