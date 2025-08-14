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
import dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TFLServiceImplTest {
    private static MemoryAppender appender;

    @MockBean
    private TFLClient mockTflClient;

    @Autowired
    public TFLHelper tflHelper;

    @Autowired
    private TFLService tflService;

    @Autowired
    private MainProperties properties;

    private Arrival first;
    private Arrival middle;
    private Arrival last;
    private List<Arrival> testArrival;

    @BeforeAll
    void setup() {
        appender = new MemoryAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();

        String defaultColour = properties.getDefaultColour();

        first = Arrival.builder().lineName("First").colour(defaultColour).destinationName("Destination1").timeToStation(50).arrivalMessage("Due").build();
        middle = Arrival.builder().lineName("Middle").colour(defaultColour).destinationName("Destination1").timeToStation(100).arrivalMessage("1m").build();
        last = Arrival.builder().lineName("Last").colour(defaultColour).destinationName("Test Destination").timeToStation(150).arrivalMessage("2m").build();

        testArrival = Arrays.asList(middle, last, first);
    }

    @AfterEach
    void cleanup() {
         appender.reset();
    }

    @Test
    void getArrivalsAreSortedByArrivalTime() {

        first.setColour("0x800000");

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
        List<Arrival> testResponse = Collections.singletonList(Arrival.builder().destinationName("New Stop").timeToStation(1).arrivalMessage("Due").colour(properties.getDefaultColour()).build());
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
        Arrival newArrival = Arrival.builder().lineName("Test123").destinationName("Test123").timeToStation(1).arrivalMessage("Due").colour(properties.getDefaultColour()).build();
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

    @Test
    void getScheduledResetConfiguration() {
        properties.setRevertToDefaultValue(99);
        properties.setRevertToDefaultTimeUnit(TimeUnit.HOURS.name());

        ScheduledResetConfiguration scheduledResetConfiguration = this.tflService.getScheduledResetConfiguration();

        assertEquals(99, scheduledResetConfiguration.getValue());
        assertEquals(TimeUnit.HOURS.name(), scheduledResetConfiguration.getTimeUnit());
    }

    @Test
    void setScheduledResetConfiguration() {
        properties.setRevertToDefaultValue(99);
        properties.setRevertToDefaultTimeUnit(TimeUnit.HOURS.name());

        ScheduledResetConfiguration newConfiguration = this.tflService.setScheduledResetConfiguration(88, "Minutes");

        assertEquals(88, newConfiguration.getValue());
        assertEquals(TimeUnit.MINUTES.name(), newConfiguration.getTimeUnit());
    }

    @Test
    void setScheduledResetConfigurationShouldValidateUnit() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> this.tflService.setScheduledResetConfiguration(88,"InvalidInit"));
        assertTrue(exception.getMessage().contains("Invalid Time Unit"),"Should throw an exception reporting timeunit is invalid");
    }

    @Test
    void setScheduledResetConfigurationShouldValidateValueIsPositive() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> this.tflService.setScheduledResetConfiguration(-1,"Hours"));
        assertTrue(exception.getMessage().contains("Value must be a positive value"),"Should throw an exception when the new value is a < 1");
    }

}