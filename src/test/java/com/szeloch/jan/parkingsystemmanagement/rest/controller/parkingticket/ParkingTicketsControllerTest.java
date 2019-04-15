package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szeloch.jan.parkingsystemmanagement.rest.model.*;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingTicketsRepository;
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

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingTicketsController.class)
public class ParkingTicketsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ParkingTicketsRepository ticketsRepository;

    @MockBean
    private ParkingTicketsUtil parkingTicketsUtil;

    private static String BASE_PATH = "http://localhost:8080/parking-tickets";
    private static final long ID = 1;
    private ParkingTicket ticket;

    @Before
    public void setup() {
        setupParkingTicketParkingClientAndVehicle();
    }

    private void setupParkingTicketParkingClientAndVehicle() {

        Locale locale = new Locale("pl", "PL");
        CreditCard creditCard = new CreditCard(Currency.getInstance(locale), 1234556789L, 112);

        ParkingClient vehicleOwner = new ParkingClient("Franek", "Sobotka", "Chmielna 3/12 Warszawa", ParkingRate.DriverType.REGULAR, creditCard);
        vehicleOwner.setId(1L);

        Vehicle vehicle = new Vehicle("ABC1234", "MAZDA", "MX5", vehicleOwner);
        vehicle.setId(1);

        ticket = new ParkingTicket();
        ticket.setId(ID);
        ticket.setIsPaid(false);
        ticket.setCost(0.0);
        ticket.setStatus(ParkingTicket.Status.INACTIVE);
        ticket.setStartTime(null);
        ticket.setEndTime(null);
        ticket.setParkedVehicle(vehicle);
    }

    @Test
    public void getReturnsCorrectResponse() throws Exception {

        given(ticketsRepository.findById(ID)).willReturn(Optional.of(ticket));

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isOk());
        verifyJson(result);
    }

    private void verifyJson(final ResultActions action) throws Exception {
        action
                .andExpect(jsonPath("parkingTicket.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("parkingTicket.isPaid", is(ticket.getIsPaid())))
                .andExpect(jsonPath("parkingTicket.cost", is(ticket.getCost())))
                .andExpect(jsonPath("parkingTicket.status", is(ticket.getStatus().toString())))
                .andExpect(jsonPath("parkingTicket.startTime", is(ticket.getStartTime())))
                .andExpect(jsonPath("parkingTicket.endTime", is(ticket.getEndTime())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.licensePlate", is(ticket.getParkedVehicle().getLicensePlate())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.companyName", is(ticket.getParkedVehicle().getCompanyName())))
                .andExpect(jsonPath("parkingTicket.parkedVehicle.modelName", is(ticket.getParkedVehicle().getModelName())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-tickets.href", is(BASE_PATH)));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {

        given(ticketsRepository.findAll()).willReturn(Collections.singletonList(ticket));

        final ResultActions result = mockMvc.perform(get(BASE_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.id", is(ticket.getId().intValue())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.isPaid", is(ticket.getIsPaid())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.cost", is(ticket.getCost())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.status", is(ticket.getStatus().toString())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.startTime", is(ticket.getStartTime())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.endTime", is(ticket.getEndTime())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.parkedVehicle.licensePlate", is(ticket.getParkedVehicle().getLicensePlate())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.parkedVehicle.companyName", is(ticket.getParkedVehicle().getCompanyName())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0].parkingTicket.parkedVehicle.modelName", is(ticket.getParkedVehicle().getModelName())))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0]._links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_embedded.parkingTicketResourceList[0]._links.parking-tickets.href", is(BASE_PATH)));
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {

        given(ticketsRepository.save(any(ParkingTicket.class))).willReturn(ticket);

        final ResultActions result = mockMvc.perform(
                post(BASE_PATH)
                        .content(mapper.writeValueAsBytes(ticket))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {

        given(ticketsRepository.save(any(ParkingTicket.class))).willReturn(ticket);

        final ResultActions result = mockMvc.perform(
                put(BASE_PATH + "/" + ID)
                        .content(mapper.writeValueAsBytes(ticket))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {

        given(ticketsRepository.findById(ID)).willReturn(Optional.of(ticket));

        mockMvc
                .perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void getParkingTicketThatDoesNotExistReturnsError() throws Exception {

        final ParkingTicketNotFoundException exception = new ParkingTicketNotFoundException(ID);

        given(ticketsRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

    @Test
    public void deleteParkingTicketThatDoesNotExistReturnsError() throws Exception {

        final ParkingTicketNotFoundException exception = new ParkingTicketNotFoundException(ID);

        given(ticketsRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(delete(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

}