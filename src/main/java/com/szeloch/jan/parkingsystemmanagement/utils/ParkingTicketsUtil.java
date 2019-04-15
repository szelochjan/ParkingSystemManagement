package com.szeloch.jan.parkingsystemmanagement.utils;

import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate;
import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRevenue;
import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingRatesRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.HOURS;

@Component
public class ParkingTicketsUtil {

    private final ParkingRatesRepository parkingRatesRepository;
    private final ParkingTicketsRepository ticketsRepository;

    public ParkingTicketsUtil(ParkingRatesRepository parkingRatesRepository, ParkingTicketsRepository ticketsRepository) {
        this.parkingRatesRepository = parkingRatesRepository;
        this.ticketsRepository = ticketsRepository;
    }

    public Double calculateParkingTicketCost(ParkingTicket ticket, ParkingRate.DriverType driverType) {

        ParkingRate parkingRate = parkingRatesRepository.findByDriverType(driverType);

        long numberOfHours = HOURS.between(ticket.getStartTime(), ticket.getEndTime());

        double totalCost = parkingRate.getFirstHourRate().doubleValue();
        double lastHourRate = parkingRate.getSecondHourRate();

        for (int i = 2; i <= numberOfHours; i++) {
            if (i == 2) {
                totalCost += lastHourRate;
            } else if (i > 2) {
                lastHourRate = lastHourRate * parkingRate.getThirdAndEachNextHourMultiplier();
                totalCost += lastHourRate;
            }
        }

        return totalCost;
    }

    public ParkingRevenue getRevenueForGivenDay(LocalDate date, Boolean paidOnly) {

        LocalDateTime givenDay = date.atStartOfDay();
        LocalDateTime nextDayStart = date.atStartOfDay().plusDays(1);

        if (paidOnly) {
            return new ParkingRevenue(date, ticketsRepository.findAllByEndTimeBetween(givenDay, nextDayStart)
                    .stream()
                    .filter(parkingTicket -> parkingTicket.getIsPaid() == paidOnly)
                    .mapToDouble(ParkingTicket::getCost)
                    .sum());
        } else {
            return new ParkingRevenue(date, ticketsRepository.findAllByEndTimeBetween(givenDay, nextDayStart)
                    .stream()
                    .mapToDouble(ParkingTicket::getCost)
                    .sum() );
        }
    }

}
