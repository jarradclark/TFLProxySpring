package dev.jarradclark.tflproxy.services.impl;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import dev.jarradclark.tflproxy.config.MainProperties;
import dev.jarradclark.tflproxy.helpers.MemoryAppender;
import dev.jarradclark.tflproxy.services.TFLClient;
import dev.jarradclark.tflproxy.services.TFLHelper;
import dev.jarradclark.tflproxy.services.TFLService;
import dev.jarradclark.tflproxy.services.model.Arrival;
import dev.jarradclark.tflproxy.services.model.ArrivalData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@SpringBootTest
class TFLServiceImplTest {
    private static MemoryAppender appender;

    Arrival first = Arrival.builder().lineName("First").destinationName("Destination1").timeToStation(50).arrivalMessage("Due").build();
    Arrival middle = Arrival.builder().lineName("Middle").destinationName("Destination1").timeToStation(100).arrivalMessage("1m").build();
    Arrival last = Arrival.builder().lineName("Last").destinationName("Test Destination").timeToStation(150).arrivalMessage("2m").build();
    List<Arrival> testArrival = Arrays.asList(middle, last, first);

    @MockBean
    private TFLClient mockTflClient;

    @Autowired
    public TFLHelper tflHelper;

    @Autowired
    private TFLService tflService;

    @Autowired
    private MainProperties properties;

    @BeforeAll
    static void setup() {
        appender = new MemoryAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
    }

    @AfterEach
    void cleanup() {
         appender.reset();
    }

    @Test
    void getArrivalsAreSortedByArrivalTime() {
        when(mockTflClient.getArrivalsForStop(this.tflService.getCurrentStop())).thenReturn(testArrival);

        List<Arrival> arrivalList = this.tflService.getArrivals().getArrivalList();
        assertEquals(first, arrivalList.getFirst());
        assertEquals(middle, arrivalList.get(1));
        assertEquals(last, arrivalList.getLast());
    }

    @Test
    void getArrivalsShouldHaveValidArrivalMessagesAdded() {
        when(mockTflClient.getArrivalsForStop(this.tflService.getCurrentStop())).thenReturn(testArrival);

        List<Arrival> arrivalList = this.tflService.getArrivals().getArrivalList();
        assertEquals("Due", arrivalList.getFirst().getArrivalMessage());
        assertEquals("1m", arrivalList.get(1).getArrivalMessage());
        assertEquals("2m", arrivalList.getLast().getArrivalMessage());
    }

    @Test
    void getArrivalsForStop() {
        List<Arrival> testResponse = Collections.singletonList(Arrival.builder().destinationName("New Stop").timeToStation(1).arrivalMessage("Due").build());
        when(this.mockTflClient.getArrivalsForStop("TestStopID")).thenReturn(testResponse);
        List<Arrival> arrivals = this.tflService.getArrivalsForStop("TestStopID").getArrivalList();
        assertEquals(testResponse, arrivals);
    }

    @Test
    void shouldSuppressErrorsFromClient() {
        when(mockTflClient.getArrivalsForStop("THROW")).thenThrow(new RestClientException("Testing"));
        List<Arrival> arrivals = this.tflService.getArrivalsForStop("TestStopID").getArrivalList();
        assertEquals(0,arrivals.size());
    }

    @Test
    void changeCurrentStop() {
        Arrival newArrival = Arrival.builder().lineName("Test123").destinationName("Test123").timeToStation(1).arrivalMessage("Due").build();
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
        Arrival newArrival = Arrival.builder().destinationName("Example for Testing").timeToStation(59).build();
        when(mockTflClient.getArrivalsForStop("DestinationTest")).thenReturn(Collections.singletonList(newArrival));
        this.tflService.setCurrentStop("DestinationTest");
        List<Arrival> arrivals = this.tflService.getArrivals().getArrivalList();
        assertEquals("Test Destination Name", arrivals.getFirst().getDestinationName());
    }

    @Test
    void shouldScheduleDefaultStopResetOnStopChange() {
        Logger logger = (Logger) LoggerFactory.getLogger(TFLServiceImpl.class);
        logger.addAppender(appender);

        properties.setRevertToDefaultValue(1);
        properties.setRevertToDefaultTimeUnit(TimeUnit.NANOSECONDS.name());

        this.tflService.setCurrentStop("TestStop");

        assertTrue(appender.contains("TFL Service set to new stop [TestStop] [Test Stop Name]", Level.INFO));
        String revertMessage = String.format("Service will reset to default in %s %s", properties.getRevertToDefaultValue(), properties.getRevertToDefaultTimeUnit());
        assertTrue(appender.contains(revertMessage, Level.DEBUG));
        // May need to consider a very short sleep here
        assertTrue(appender.contains(String.format("Scheduled reset of current stop back to default [%s]",properties.getDefaultStop()), Level.INFO));
    }

    @Test
    void willCancelScheduledResetOnNewRequest() {
        Logger logger = (Logger) LoggerFactory.getLogger(TFLServiceImpl.class);
        logger.addAppender(appender);

        properties.setRevertToDefaultValue(1);
        properties.setRevertToDefaultTimeUnit(TimeUnit.HOURS.name());

        this.tflService.setCurrentStop("TestStop");
        this.tflService.setCurrentStop("TestStop");

        assertTrue(appender.contains("Canceling existing scheduled task", Level.DEBUG));
    }

}