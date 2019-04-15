package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szeloch.jan.parkingsystemmanagement.rest.model.*;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingClientsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.VehiclesRepository;
import com.szeloch.jan.parkingsystemmanagement.utils.ParkingTicketsUtil;
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

import static com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket.Status.ACTIVE;
import static com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingTicket.Status.CLOSED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingClientsController.class)
public class ParkingClientsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ParkingClientsRepository clientsRepository;

    @MockBean
    private VehiclesRepository vehiclesRepository;

    @MockBean
    private ParkingTicketsRepository ticketsRepository;

    @MockBean
    private ParkingTicketsUtil ticketsUtil;

    private static String BASE_PATH = "http://localhost:8080/parking-clients";
    private static String VEHICLES_PATH = "/vehicles";
    private static String VEHICLES_BASE_PATH = "http://localhost:8080/vehicles";
    private static String PARKING_TICKET_PATH = "/parking-ticket";
    private static String ACTIVATE_PATH = "/activate";
    private static String CLOSE_PATH = "/close";
    private static String PARKING_TICKETS_BASE_PATH = "http://localhost:8080/parking-tickets";

    private static final long ID = 1;
    private ParkingClient client;
    private ParkingTicket ticket;
    private Vehicle vehicle;

    @Before
    public void setup() {
        setupParkingClientAndVehicle();
    }

    private void setupParkingClientAndVehicle() {

        Locale locale = new Locale("pl", "PL");
        CreditCard creditCard = new CreditCard(Currency.getInstance(locale), 1234556789L, 112);

        client = new ParkingClient();
        client.setId(ID);
        client.setFirstName("Franek");
        client.setLastName("Sobotka");
        client.setAddress("Chmielna 3/12 Warszawa");
        client.setDriverType(ParkingRate.DriverType.REGULAR);
        client.setCreditCard(creditCard);

        ticket = new ParkingTicket();
        ticket.setId(ID);
        ticket.setIsPaid(false);
        ticket.setCost(0.0);
        ticket.setStatus(ParkingTicket.Status.INACTIVE);
        ticket.setStartTime(null);
        ticket.setEndTime(null);

        vehicle = new Vehicle("ABC1234", "MAZDA", "MX5", client);
        vehicle.setId((int) ID);
        vehicle.setParkingTicket(ticket);
        ticket.setParkedVehicle(vehicle);

        client.setVehicles(Collections.singletonList(vehicle));
    }

    @Test
    public void getReturnsCorrectResponse() throws Exception {

        given(clientsRepository.findById(ID)).willReturn(Optional.of(client));

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isOk());
        verifyJson(result);
    }

    private void verifyJson(final ResultActions action) throws Exception {
        action
                .andExpect(jsonPath("parkingClient.id", is(client.getId().intValue())))
                .andExpect(jsonPath("parkingClient.firstName", is(client.getFirstName())))
                .andExpect(jsonPath("parkingClient.lastName", is(client.getLastName())))
                .andExpect(jsonPath("parkingClient.address", is(client.getAddress())))
                .andExpect(jsonPath("parkingClient.driverType", is(client.getDriverType().toString())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-clients.href", is(BASE_PATH)))
                .andExpect(jsonPath("_links.parking-client-vehicles.href", is(BASE_PATH + "/" + ID + VEHICLES_PATH)));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {

        given(clientsRepository.findAll()).willReturn(Collections.singletonList(client));

        final ResultActions result = mockMvc.perform(get(BASE_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0].parkingClient.id", is(client.getId().intValue())))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0].parkingClient.firstName", is(client.getFirstName())))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0].parkingClient.lastName", is(client.getLastName())))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0].parkingClient.address", is(client.getAddress())))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0].parkingClient.driverType", is(client.getDriverType().toString())))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0]._links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0]._links.parking-clients.href", is(BASE_PATH)))
                .andExpect(jsonPath("_embedded.parkingClientResourceList[0]._links.parking-client-vehicles.href", is(BASE_PATH + "/" + ID + VEHICLES_PATH)));
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {

        given(clientsRepository.save(any(ParkingClient.class))).willReturn(client);

        final ResultActions result = mockMvc.perform(post(BASE_PATH)
                .content(mapper.writeValueAsBytes(client))
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {

        given(clientsRepository.save(any(ParkingClient.class))).willReturn(client);

        final ResultActions result =
                mockMvc.perform(
                        put(BASE_PATH + "/" + ID)
                                .content(mapper.writeValueAsBytes(client))
                                .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {

        given(clientsRepository.findById(ID)).willReturn(Optional.of(client));

        mockMvc
                .perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void getParkingClientThatDoesNotExistReturnsError() throws Exception {

        final ParkingClientNotFoundException exception = new ParkingClientNotFoundException(ID);

        given(clientsRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

    @Test
    public void deleteParkingClientThatDoesNotExistReturnsError() throws Exception {

        final ParkingClientNotFoundException exception = new ParkingClientNotFoundException(ID);

        given(clientsRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(delete(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

    @Test
    public void getAllParkingClientVehiclesReturnsCorrectResponse() throws Exception {

        given(vehiclesRepository.findByOwnerId(ID)).willReturn(Collections.singletonList(vehicle));

        final ResultActions result = mockMvc.perform(
                get(BASE_PATH + "/" + ID + VEHICLES_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("_embedded.vehicleList[0].id", is(vehicle.getId())))
                .andExpect(jsonPath("_embedded.vehicleList[0].licensePlate", is(vehicle.getLicensePlate())))
                .andExpect(jsonPath("_embedded.vehicleList[0].companyName", is(vehicle.getCompanyName())))
                .andExpect(jsonPath("_embedded.vehicleList[0].modelName", is(vehicle.getModelName())))
                .andExpect(jsonPath("_embedded.vehicleList[0]._links.self.href",
                        is(VEHICLES_BASE_PATH + "/" + ID)))

                .andExpect(jsonPath("_embedded.vehicleList[0]._links.parking-ticket[0].href",
                        is(VEHICLES_BASE_PATH + "/" + ID + PARKING_TICKET_PATH)))

                .andExpect(jsonPath("_embedded.vehicleList[0]._links.parking-ticket[1].href",
                        is(BASE_PATH + "/" + ID + VEHICLES_PATH + "/" + ID + PARKING_TICKET_PATH)))

                .andExpect(jsonPath("_embedded.vehicleList[0]._links.activate-parking-ticket.href",
                        is(BASE_PATH + "/" + ID + VEHICLES_PATH + "/" + ID + PARKING_TICKET_PATH + ACTIVATE_PATH)))

                .andExpect(jsonPath("_embedded.vehicleList[0]._links.close-parking-ticket.href",
                is(BASE_PATH + "/" + ID + VEHICLES_PATH + "/" + ID + PARKING_TICKET_PATH + CLOSE_PATH)))

                .andExpect(jsonPath("_links.vehicles.href", is(VEHICLES_BASE_PATH)));
    }

    @Test
    public void getParkingTicketForVehicle() throws Exception {

        given(clientsRepository.findById(ID)).willReturn(Optional.of(client));

        final ResultActions result = mockMvc.perform(
                get(BASE_PATH + "/" + ID + VEHICLES_PATH + "/" + ID + PARKING_TICKET_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("parkingTicket.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("parkingTicket.isPaid", is(ticket.getIsPaid())))
                .andExpect(jsonPath("parkingTicket.cost", is(ticket.getCost())))
                .andExpect(jsonPath("parkingTicket.status", is(ticket.getStatus().toString())))
                .andExpect(jsonPath("parkingTicket.startTime", is(ticket.getStartTime())))
                .andExpect(jsonPath("parkingTicket.endTime", is(ticket.getEndTime())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.licensePlate", is(ticket.getParkedVehicle().getLicensePlate())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.companyName", is(ticket.getParkedVehicle().getCompanyName())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.modelName", is(ticket.getParkedVehicle().getModelName())))
                .andExpect(jsonPath("_links.self.href", is(PARKING_TICKETS_BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-tickets.href", is(PARKING_TICKETS_BASE_PATH)));
    }

    @Test
    public void activateParkingTicketForVehicleReturnsCorrectResponse() throws Exception {

        given(clientsRepository.findById(ID)).willReturn(Optional.of(client));
        given(ticketsRepository.save(ticket)).willReturn(ticket);

        final ResultActions result = mockMvc.perform(
                put(BASE_PATH + "/" + ID + VEHICLES_PATH + "/" + ID + PARKING_TICKET_PATH + ACTIVATE_PATH));

        result.andExpect(status().isOk());

        result
                .andExpect(jsonPath("parkingTicket.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("parkingTicket.isPaid", is(ticket.getIsPaid())))
                .andExpect(jsonPath("parkingTicket.cost", is(ticket.getCost())))
                .andExpect(jsonPath("parkingTicket.status", is(ACTIVE.toString())))
                .andExpect(jsonPath("parkingTicket.endTime", is(ticket.getEndTime())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.licensePlate", is(ticket.getParkedVehicle().getLicensePlate())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.companyName", is(ticket.getParkedVehicle().getCompanyName())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.modelName", is(ticket.getParkedVehicle().getModelName())))
                .andExpect(jsonPath("_links.self.href", is(PARKING_TICKETS_BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-tickets.href", is(PARKING_TICKETS_BASE_PATH)));
    }

    @Test
    public void closeParkingTicketForVehicleReturnsCorrectResponse() throws Exception {

        ticket.setStatus(ACTIVE);

        given(clientsRepository.findById(ID)).willReturn(Optional.of(client));
        given(ticketsRepository.save(ticket)).willReturn(ticket);

        final ResultActions result = mockMvc.perform(
                delete(BASE_PATH + "/" + ID + VEHICLES_PATH + "/" + ID + PARKING_TICKET_PATH + CLOSE_PATH));

        result.andExpect(status().isOk());

        result
                .andExpect(jsonPath("parkingTicket.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("parkingTicket.isPaid", is(ticket.getIsPaid())))
                .andExpect(jsonPath("parkingTicket.cost", is(ticket.getCost())))
                .andExpect(jsonPath("parkingTicket.status", is(CLOSED.toString())))
                .andExpect(jsonPath("parkingTicket.endTime", is(ticket.getEndTime().toString())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.licensePlate", is(ticket.getParkedVehicle().getLicensePlate())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.companyName", is(ticket.getParkedVehicle().getCompanyName())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.modelName", is(ticket.getParkedVehicle().getModelName())))
                .andExpect(jsonPath("_links.self.href", is(PARKING_TICKETS_BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-tickets.href", is(PARKING_TICKETS_BASE_PATH)));

    }

}