package api.jarradclark.dev.TFLProxySpring.services.impl;

import api.jarradclark.dev.TFLProxySpring.services.TFLClient;
import api.jarradclark.dev.TFLProxySpring.services.TFLService;
import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TFLServiceImplTest {

    Arrival first = Arrival.builder().lineName("First").destinationName("Destination1").timeToStation(50).build();
    Arrival middle = Arrival.builder().lineName("Middle").destinationName("Destination1").timeToStation(100).build();
    Arrival last = Arrival.builder().lineName("Last").destinationName("Test Destination").timeToStation(150).build();
    List<Arrival> testArrival = Arrays.asList(middle, last, first);

    @Test
    void getArrivalsAreSortedByArrivalTime() {
        TFLClient tflClient = mock(TFLClient.class);

        TFLService tflService = new TFLServiceImpl(tflClient);
        when(tflClient.getArrivalsForStop(tflService.getCurrentStop())).thenReturn(testArrival);

        List<Arrival> arrivals = tflService.getArrivals();
        assertEquals(first, arrivals.getFirst());
        assertEquals(middle, arrivals.get(1));
        assertEquals(last, arrivals.getLast());
    }

    @Test
    void getArrivalsForStop() {
        TFLClient tflClient = mock(TFLClient.class);

        TFLService tflService = new TFLServiceImpl(tflClient);
        List<Arrival> testResponse = Collections.singletonList(Arrival.builder().destinationName("New Stop").build());
        when(tflClient.getArrivalsForStop("TestStopID")).thenReturn(testResponse);
        List<Arrival> arrivals = tflService.getArrivalsForStop("TestStopID");
        assertEquals(testResponse, arrivals);
    }


    @Test
    void changeCurrentStop() {
        TFLClient tflClient = mock(TFLClient.class);
        Arrival newArrival = Arrival.builder().lineName("Test123").destinationName("Test123").timeToStation(1).build();
        when(tflClient.getArrivalsForStop("NewStopID")).thenReturn(Collections.singletonList(newArrival));

        TFLService tflService = new TFLServiceImpl(tflClient);
        tflService.setCurrentStop("NewStopID");
        List<Arrival> arrivals = tflService.getArrivals();
        assertEquals(newArrival, arrivals.getFirst());
    }
}