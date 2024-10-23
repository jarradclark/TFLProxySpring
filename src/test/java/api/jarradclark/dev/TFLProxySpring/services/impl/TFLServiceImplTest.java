package api.jarradclark.dev.TFLProxySpring.services.impl;


import api.jarradclark.dev.TFLProxySpring.services.TFLClient;
import api.jarradclark.dev.TFLProxySpring.services.TFLService;
import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;
import api.jarradclark.dev.TFLProxySpring.services.model.ArrivalData;
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
    private  TFLService tflService;

    @Test
    void getArrivalsAreSortedByArrivalTime() {
        when(mockTflClient.getArrivalsForStop(tflService.getCurrentStop())).thenReturn(testArrival);

        List<Arrival> arrivalList = tflService.getArrivals().getArrivalList();
        assertEquals(first, arrivalList.getFirst());
        assertEquals(middle, arrivalList.get(1));
        assertEquals(last, arrivalList.getLast());
    }

    @Test
    void getArrivalsForStop() {
        TFLService tflService = new TFLServiceImpl(mockTflClient);
        List<Arrival> testResponse = Collections.singletonList(Arrival.builder().destinationName("New Stop").build());
        when(mockTflClient.getArrivalsForStop("TestStopID")).thenReturn(testResponse);
        List<Arrival> arrivals = tflService.getArrivalsForStop("TestStopID").getArrivalList();
        assertEquals(testResponse, arrivals);
    }


    @Test
    void changeCurrentStop() {
        Arrival newArrival = Arrival.builder().lineName("Test123").destinationName("Test123").timeToStation(1).build();
        when(mockTflClient.getArrivalsForStop("NewStopID")).thenReturn(Collections.singletonList(newArrival));

        TFLService tflService = new TFLServiceImpl(mockTflClient);
        tflService.setCurrentStop("NewStopID");
        List<Arrival> arrivals = tflService.getArrivals().getArrivalList();
        assertEquals(newArrival, arrivals.getFirst());
    }

    @Test
    void testStationMappingChanges() {
        TFLService service = new TFLServiceImpl(mockTflClient);
        ArrivalData arrivalData = service.getArrivalsForStop("TestStop");
        assertEquals("Test Stop Name", arrivalData.getStopName());
    }

    @Test
    void testDestinationNameChanges() {
        Arrival newArrival = Arrival.builder().destinationName("Example for Testing").build();
        when(mockTflClient.getArrivalsForStop("DestinationTest")).thenReturn(Collections.singletonList(newArrival));

        TFLService tflService = new TFLServiceImpl(mockTflClient);
        tflService.setCurrentStop("DestinationTest");
        List<Arrival> arrivals = tflService.getArrivals().getArrivalList();
        assertEquals("Test Destination Name", arrivals.getFirst().getDestinationName());
    }

}