package dev.jarradclark.api.TFLProxySpring.controllers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import dev.jarradclark.api.TFLProxySpring.config.MainProperties;
import dev.jarradclark.api.TFLProxySpring.services.TFLService;
import dev.jarradclark.api.TFLProxySpring.services.model.Arrival;
import dev.jarradclark.api.TFLProxySpring.services.model.ArrivalData;
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

@WebMvcTest(TFLContoller.class)
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

        this.mockMvc.perform(get("/allArrivals").headers(authHeader))
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

        this.mockMvc.perform(get("/arrivals/DiffStopId").headers(authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.arrivalList",hasSize(1)))
                .andExpect(jsonPath("$.arrivalList.[0].destinationName").value("Diff Testing"));
    }

    @Test
    void changeStop() throws Exception{
        this.mockMvc.perform(post("/changeCurrentStop/NewStopId").headers(authHeader))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Stop Changed to NewStopId"));
        verify(service, atLeast(1)).setCurrentStop("NewStopId");
    }

    @Test
    void shouldReturn401WhenNoAPIKey() throws Exception {
        this.mockMvc.perform(get("/allArrivals"))
                .andExpect(status().isUnauthorized());
    }

}