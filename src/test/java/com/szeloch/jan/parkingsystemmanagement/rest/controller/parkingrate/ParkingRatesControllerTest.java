package com.szeloch.jan.parkingsystemmanagement.rest.controller.parkingrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szeloch.jan.parkingsystemmanagement.rest.model.ParkingRate;
import com.szeloch.jan.parkingsystemmanagement.rest.repository.ParkingRatesRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ParkingRatesController.class)
public class ParkingRatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ParkingRatesRepository ratesRepository;

    private static String BASE_PATH = "http://localhost:8080/parking-rates";
    private static final int ID = 1;
    private ParkingRate parkingRate;

    @Before
    public void setup() {
        setupParkingRate();
    }

    private void setupParkingRate() {

        parkingRate = new ParkingRate();
        parkingRate.setId(ID);
        parkingRate.setFirstHourRate(1);
        parkingRate.setSecondHourRate(2);
        parkingRate.setThirdAndEachNextHourMultiplier(1.5);
        parkingRate.setDriverType(ParkingRate.DriverType.REGULAR);
    }

    @Test
    public void getReturnsCorrectResponse() throws Exception {

        given(ratesRepository.findById(ID)).willReturn(Optional.of(parkingRate));

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isOk());
        verifyJson(result);
    }

    private void verifyJson(ResultActions action) throws Exception {
        action
                .andExpect(jsonPath("parkingRate.id", is(parkingRate.getId())))
                .andExpect(jsonPath("parkingRate.firstHourRate", is(parkingRate.getFirstHourRate())))
                .andExpect(jsonPath("parkingRate.secondHourRate", is(parkingRate.getSecondHourRate())))
                .andExpect(jsonPath("parkingRate.thirdAndEachNextHourMultiplier", is(parkingRate.getThirdAndEachNextHourMultiplier())))
                .andExpect(jsonPath("parkingRate.driverType", is(parkingRate.getDriverType().toString())))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_links.parking-rates.href", is(BASE_PATH)));
    }

    @Test
    public void getAllReturnsCorrectResponse() throws Exception {

        given(ratesRepository.findAll()).willReturn(Collections.singletonList(parkingRate));

        final ResultActions result = mockMvc.perform(get(BASE_PATH));

        result.andExpect(status().isOk());
        result
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0].parkingRate.id", is(parkingRate.getId())))
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0].parkingRate.firstHourRate", is(parkingRate.getFirstHourRate())))
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0].parkingRate.secondHourRate", is(parkingRate.getSecondHourRate())))
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0].parkingRate.thirdAndEachNextHourMultiplier", is(parkingRate.getThirdAndEachNextHourMultiplier())))
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0].parkingRate.driverType", is(parkingRate.getDriverType().toString())))
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0]._links.self.href", is(BASE_PATH + "/" + ID)))
                .andExpect(jsonPath("_embedded.parkingRateResourceList[0]._links.parking-rates.href", is(BASE_PATH )))
                .andExpect(jsonPath("_links.self.href", is(BASE_PATH)));
    }

    @Test
    public void postReturnsCorrectResponse() throws Exception {

        given(ratesRepository.save(any(ParkingRate.class))).willReturn(parkingRate);

        final ResultActions result = mockMvc.perform(
                post(BASE_PATH)
                .content(mapper.writeValueAsBytes(parkingRate))
                .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void putReturnsCorrectResponse() throws Exception {

        given(ratesRepository.save(any(ParkingRate.class))).willReturn(parkingRate);

        final ResultActions result =
                mockMvc.perform(
                        put(BASE_PATH + "/" + ID)
                        .content(mapper.writeValueAsBytes(parkingRate))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isCreated());
        verifyJson(result);
    }

    @Test
    public void deleteReturnsCorrectResponse() throws Exception {

        given(ratesRepository.findById(ID)).willReturn(Optional.of(parkingRate));

        mockMvc
                .perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void getParkingRateThatDoesNotExistReturnsError() throws Exception {

        final ParkingRateNotFoundException exception = new ParkingRateNotFoundException(ID);

        given(ratesRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

    @Test
    public void deleteParkingRateThatDoesNotExistReturnsError() throws Exception {

        final ParkingRateNotFoundException exception = new ParkingRateNotFoundException(ID);

        given(ratesRepository.findById(ID)).willReturn(Optional.empty());

        final ResultActions result = mockMvc.perform(delete(BASE_PATH + "/" + ID));

        result.andExpect(status().isNotFound());
        result
                .andExpect(jsonPath("$[0].logref", is(String.valueOf(ID))))
                .andExpect(jsonPath("$[0].message", is(exception.getMessage())))
                .andExpect(jsonPath("$[0].links", is(new ArrayList<String>())));
    }

}