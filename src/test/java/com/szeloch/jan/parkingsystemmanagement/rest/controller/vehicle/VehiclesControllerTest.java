package com.szeloch.jan.parkingsystemmanagement.rest.controller.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szeloch.jan.parkingsystemmanagement.rest.model.*;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingClientsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.VehiclesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(VehiclesController.class)
public class VehiclesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private VehiclesRepository vehiclesRepository;

    @MockBean
    private ParkingClientsRepository clientsRepository;

    @MockBean
    private ParkingTicketsRepository ticketsRepository;

    private static String BASE_PATH = "http://localhost:8080/vehicles";
    private static String PARKING_CLIENTS_BASE_PATH = "http://localhost:8080/parking-clients";
    private static String PARKING_TICKETS_BASE_PATH = "http://localhost:8080/parking-tickets";
    private static String OWNER_PATH = "/owner";
    private static String PARKING_TICKET_PATH = "/parking-ticket";
    private static String VEHICLES_PATH = "/vehicles";
    private static final int ID = 1;
    private Vehicle vehicle;

    @Before
    public void setup() {
        setupVehicle();
    }

    private void setupVehicle() {

        Locale locale = new Locale("pl", "PL");
        CreditCard creditCard = new CreditCard(Currency.getInstance(locale), 1234556789L, 112);

        ParkingClient vehicleOwner = new ParkingClient("Franek", "Sobotka", "Chmielna 3/12 Warszawa", ParkingRate.DriverType.REGULAR, creditCard);
        vehicleOwner.setId(1L);

        vehicle = new Vehicle();
        vehicle.setId(ID);
        vehicle.setModelName("MX5");
        vehicle.setCompanyName("MAZDA");
        vehicle.setLicensePlate("ABC1234");
        vehicle.setOwner(vehicleOwner);

        ParkingTicket ticket = new ParkingTicket();
        ticket.setId(1L);
        ticket.setIsPaid(false);
        ticket.setCost(0.0);
        ticket.setStatus(ParkingTicket.Status.INACTIVE);
        ticket.setStartTime(null);
        ticket.setEndTime(null);
        ticket.setParkedVehicle(vehicle);

        vehicle.setParkingTicket(ticket);
    }

    @Test
    public void getReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.findById(ID)).willReturn(Optional.of(vehicle));

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isOk());
        verifyJson(result);
    }

    private void verifyJson(ResultActions action) throws Exception {
        action
                .andExpect(jsonPath("vehicle.id", is(vehicle.getId())))
                .andExpect(jsonPath("vehicle.licensePlate", is(vehicle.getLicensePlate())))
                .andExpect(jsonPath("vehicle.companyName", is(vehicle.getCompanyName())))
                .andExpect(jsonPath("vehicle.modelName", is(vehicle.getModelName())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.owner.href", is(BASE_PATH + "/" + ID + OWNER_PATH)))
                .andExpect(jsonPath("_links.parking-ticket.href", is(BASE_PATH + "/" + ID + PARKING_TICKET_PATH)));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.findAll()).willReturn(Collections.singletonList(vehicle));

        final ResultActions result = mockMvc.perform(get(BASE_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("_embedded.vehicleResourceList[0].vehicle.id", is(vehicle.getId())))
                .andExpect(jsonPath("_embedded.vehicleResourceList[0].vehicle.licensePlate", is(vehicle.getLicensePlate())))
                .andExpect(jsonPath("_embedded.vehicleResourceList[0].vehicle.companyName", is(vehicle.getCompanyName())))
                .andExpect(jsonPath("_embedded.vehicleResourceList[0].vehicle.modelName", is(vehicle.getModelName())))
                .andExpect(jsonPath("_embedded.vehicleResourceList[0]._links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_embedded.vehicleResourceList[0]._links.owner.href", is(BASE_PATH + "/" + ID + OWNER_PATH)))
                .andExpect(jsonPath("_embedded.vehicleResourceList[0]._links.parking-ticket.href", is(BASE_PATH + "/" + ID + PARKING_TICKET_PATH)));
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.save(any(Vehicle.class))).willReturn(vehicle);

        final ResultActions result = mockMvc.perform(
                post(BASE_PATH)
                        .content(mapper.writeValueAsBytes(vehicle))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.save(any(Vehicle.class))).willReturn(vehicle);

        final ResultActions result =
                mockMvc.perform(
                        put(BASE_PATH + "/" + ID)
                                .content(mapper.writeValueAsBytes(vehicle))
                                .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.findById(ID)).willReturn(Optional.of(vehicle));

        mockMvc
                .perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void getVehicleThatDoesNotExistReturnsError() throws Exception {

        final VehicleNotFoundException exception = new VehicleNotFoundException(ID);

        given(vehiclesRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

    @Test
    public void getVehicleOwnerReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.findById(ID)).willReturn(Optional.of(vehicle));

        final ResultActions result = mockMvc.perform(
                get(BASE_PATH + "/" + ID + OWNER_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("parkingClient.id", is(vehicle.getOwner().getId().intValue())))
                .andExpect(jsonPath("parkingClient.firstName", is(vehicle.getOwner().getFirstName())))
                .andExpect(jsonPath("parkingClient.lastName", is(vehicle.getOwner().getLastName())))
                .andExpect(jsonPath("parkingClient.address", is(vehicle.getOwner().getAddress())))
                .andExpect(jsonPath("parkingClient.driverType", is(vehicle.getOwner().getDriverType().toString())))
                .andExpect(jsonPath("_links.self.href", is(PARKING_CLIENTS_BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-clients.href", is(PARKING_CLIENTS_BASE_PATH)))
                .andExpect(jsonPath("_links.parking-client-vehicles.href", is(PARKING_CLIENTS_BASE_PATH + "/" + ID + VEHICLES_PATH)));
    }

    @Test
    public void getVehicleParkingTicketReturnsCorrect() throws Exception {

        given(vehiclesRepository.findById(ID)).willReturn(Optional.of(vehicle));

        final ResultActions result = mockMvc.perform(
          get(BASE_PATH + "/" + ID + PARKING_TICKET_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("parkingTicket.id", is(vehicle.getParkingTicket().getId().intValue())))
                .andExpect(jsonPath("parkingTicket.isPaid", is(vehicle.getParkingTicket().getIsPaid())))
                .andExpect(jsonPath("parkingTicket.cost", is(vehicle.getParkingTicket().getCost())))
                .andExpect(jsonPath("parkingTicket.status", is(vehicle.getParkingTicket().getStatus().toString())))
                .andExpect(jsonPath("parkingTicket.startTime", is(vehicle.getParkingTicket().getStartTime())))
                .andExpect(jsonPath("parkingTicket.endTime", is(vehicle.getParkingTicket().getEndTime())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.id", is(vehicle.getId())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.licensePlate", is(vehicle.getLicensePlate())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.companyName", is(vehicle.getCompanyName())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.modelName", is(vehicle.getModelName())))
                .andExpect(jsonPath("_links.self.href", is(PARKING_TICKETS_BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-tickets.href", is(PARKING_TICKETS_BASE_PATH)));
    }

}