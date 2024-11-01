package dev.jarradclark.api.TFLProxySpring.services.impl;


import dev.jarradclark.api.TFLProxySpring.config.MainProperties;
import dev.jarradclark.api.TFLProxySpring.services.TFLClient;
import dev.jarradclark.api.TFLProxySpring.services.TFLHelper;
import dev.jarradclark.api.TFLProxySpring.services.TFLService;
import dev.jarradclark.api.TFLProxySpring.services.model.Arrival;
import dev.jarradclark.api.TFLProxySpring.services.model.ArrivalData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@SpringBootTest
class TFLServiceImplTest {

    Arrival first = Arrival.builder().lineName("First").destinationName("Destination1").timeToStation(50).build();
    Arrival middle = Arrival.builder().lineName("Middle").destinationName("Destination1").timeToStation(100).build();
    Arrival last = Arrival.builder().lineName("Last").destinationName("Test Destination").timeToStation(150).build();
    List<Arrival> testArrival = Arrays.asList(middle, last, first);

    @MockBean
    private TFLClient mockTflClient;

    @Autowired
    public TFLHelper tflHelper;

    @Autowired
    private TFLService tflService;

    @Autowired
    private MainProperties properties;

    @Test
    void getArrivalsAreSortedByArrivalTime() {
        when(mockTflClient.getArrivalsForStop(this.tflService.getCurrentStop())).thenReturn(testArrival);

        List<Arrival> arrivalList = this.tflService.getArrivals().getArrivalList();
        assertEquals(first, arrivalList.getFirst());
        assertEquals(middle, arrivalList.get(1));
        assertEquals(last, arrivalList.getLast());
    }

    @Test
    void getArrivalsForStop() {
        TFLService tflService = new TFLServiceImpl(this.mockTflClient, properties);
        List<Arrival> testResponse = Collections.singletonList(Arrival.builder().destinationName("New Stop").build());
        when(this.mockTflClient.getArrivalsForStop("TestStopID")).thenReturn(testResponse);
        List<Arrival> arrivals = this.tflService.getArrivalsForStop("TestStopID").getArrivalList();
        assertEquals(testResponse, arrivals);
    }


    @Test
    void changeCurrentStop() {
        Arrival newArrival = Arrival.builder().lineName("Test123").destinationName("Test123").timeToStation(1).build();
        when(this.mockTflClient.getArrivalsForStop("NewStopID")).thenReturn(Collections.singletonList(newArrival));

        this.tflService.setCurrentStop("NewStopID");
        List<Arrival> arrivals = this.tflService.getArrivals().getArrivalList();
        assertEquals(newArrival, arrivals.getFirst());
    }

    @Test
    void testStationMappingChanges() {
        ArrivalData arrivalData = this.tflService.getArrivalsForStop("TestStop");
        assertEquals("Test Stop Name", arrivalData.getStopName());
    }

    @Test
    void testDestinationNameChanges() {
        Arrival newArrival = Arrival.builder().destinationName("Example for Testing").build();
        when(mockTflClient.getArrivalsForStop("DestinationTest")).thenReturn(Collections.singletonList(newArrival));
        this.tflService.setCurrentStop("DestinationTest");
        List<Arrival> arrivals = this.tflService.getArrivals().getArrivalList();
        assertEquals("Test Destination Name", arrivals.getFirst().getDestinationName());
    }

}