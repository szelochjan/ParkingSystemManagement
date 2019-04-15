package com.szeloch.jan.parkingsystemmanagement;

import com.szeloch.jan.parkingsystemmanagement.rest.model.*;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingClientsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingRatesRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.VehiclesRepository;
import com.szeloch.jan.parkingsystemmanagement.utils.ParkingTicketsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;

import static com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate.DriverType.DISABLED;
import static com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate.DriverType.REGULAR;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(ParkingClientsRepository parkingClientsRepository,
                                   VehiclesRepository vehiclesRepository,
                                   ParkingRatesRepository parkingRatesRepository,
                                   ParkingTicketsRepository ticketsRepository,
                                   ParkingTicketsUtil ticketsUtil) {

        Locale locale = new Locale("pl", "PL");
        CreditCard creditCard_1 = new CreditCard(Currency.getInstance(locale), 1234556789L, 112);
        CreditCard creditCard_2 = new CreditCard(Currency.getInstance(locale), 7830120012L, 543);

        ParkingClient client_1 = new ParkingClient("Jan", "Kowalski", "Smolna 23/15", REGULAR, creditCard_1);

        ParkingClient client_2 = new ParkingClient("Tomasz", "Nowak", "Prosta 14/3", DISABLED, creditCard_2);

        Vehicle vehicle_1 = new Vehicle("ABC1234", "Skoda", "Fabia");
        Vehicle vehicle_2 = new Vehicle("CDE5678", "Lamborghini", "Diablo");
        Vehicle vehicle_3 = new Vehicle("KJH5634", "Porsche", "911");

        ParkingTicket parkingTicket = vehicle_1.getParkingTicket();
        parkingTicket.setStartTime(LocalDateTime.now().minusHours(3));
        parkingTicket.setStatus(ParkingTicket.Status.CLOSED);
        parkingTicket.setEndTime(LocalDateTime.now().plusHours(4));
        parkingTicket.setCost(200.00);

        ParkingTicket parkingTicket2 = vehicle_2.getParkingTicket();
        parkingTicket2.setStartTime(LocalDateTime.now().minusHours(3));
        parkingTicket2.setStatus(ParkingTicket.Status.CLOSED);
        parkingTicket2.setEndTime(LocalDateTime.now().plusHours(4));
        parkingTicket2.setCost(400.00);

        client_1.addVehicle(vehicle_1);
        client_1.addVehicle(vehicle_2);
        client_2.addVehicle(vehicle_3);

        vehicle_1.setOwner(client_1);
        vehicle_2.setOwner(client_1);
        vehicle_3.setOwner(client_2);

        ParkingRate parkingRateForRegularDriver = new ParkingRate(REGULAR, 1, 2, 1.5);
        ParkingRate parkingRateForDisabledDriver = new ParkingRate(DISABLED, 0, 2, 1.2);

        return args -> {
            log.info("Preloading " + parkingClientsRepository.save(client_1));
            log.info("Preloading " + parkingClientsRepository.save(client_2));
            log.info("Preloading " + vehiclesRepository.save(vehicle_1));
            log.info("Preloading " + vehiclesRepository.save(vehicle_2));
            log.info("Preloading " + vehiclesRepository.save(vehicle_3));
            log.info("Preloading " + parkingRatesRepository.save(parkingRateForRegularDriver));
            log.info("Preloading " + parkingRatesRepository.save(parkingRateForDisabledDriver));
        };

    }

}
