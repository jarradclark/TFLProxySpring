package dev.jarradclark.tflproxy.controllers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import dev.jarradclark.tflproxy.config.MainProperties;
import dev.jarradclark.tflproxy.services.TFLService;
import dev.jarradclark.tflproxy.services.model.Arrival;
import dev.jarradclark.tflproxy.services.model.ArrivalData;
import dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TFLController.class)
@EnableConfigurationProperties(MainProperties.class)
class TFLControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TFLService service;

    private final HttpHeaders authHeader = new HttpHeaders();
    {
        authHeader.set("API-Key", "DEV_API_KEY");
    }

    @Test
    @DisplayName("Should Get the results from the Service Layer for the current stop")
    void listArrivals() throws Exception {
        List<Arrival> arrivalList = List.of(Arrival.builder().destinationName("Testing").build());
        ArrivalData arrivalData = ArrivalData.builder().arrivalList(arrivalList).build();
        when(service.getArrivals()).thenReturn(arrivalData);

        mockMvc.perform(get("/allArrivals").headers(authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.arrivalList",hasSize(1)))
                .andExpect(jsonPath("$.arrivalList.[0].destinationName").value("Testing"));
    }

    @Test
    @DisplayName("Should Get the results from the Service Layer for a specific stop")
    void listArrivalsForStop() throws Exception {
        List<Arrival> arrivalList = List.of(Arrival.builder().destinationName("Diff Testing").build());
        ArrivalData arrivalData = ArrivalData.builder().arrivalList(arrivalList).build();
        when(service.getArrivalsForStop("DiffStopId")).thenReturn(arrivalData);

        mockMvc.perform(get("/arrivals/DiffStopId").headers(authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.arrivalList",hasSize(1)))
                .andExpect(jsonPath("$.arrivalList.[0].destinationName").value("Diff Testing"));
    }

    @Test
    void changeStop() throws Exception{
        mockMvc.perform(post("/changeCurrentStop/NewStopId").headers(authHeader))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Stop Changed to NewStopId"));
        verify(service, atLeast(1)).setCurrentStop("NewStopId");
    }

    @Test
    @DisplayName("Should return current scheduled reset configuration")
    void currentScheduledResetConfiguration() throws Exception {
        ScheduledResetConfiguration configuration = ScheduledResetConfiguration.builder().unit("TestUnit").value(87).build();

        when(service.getCurrentScheduledResetConfiguration()).thenReturn(configuration);

        mockMvc.perform(get("/currentScheduledResetConfiguration").headers(authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(87))
                .andExpect(jsonPath("$.unit").value("TestUnit"));
    }

    @Test
    void shouldReturn401WhenNoAPIKey() throws Exception {
        mockMvc.perform(get("/allArrivals"))
                .andExpect(status().isUnauthorized());
    }

}