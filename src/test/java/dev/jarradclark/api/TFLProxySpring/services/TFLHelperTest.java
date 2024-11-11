package dev.jarradclark.api.TFLProxySpring.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TFLHelperTest {

    @Autowired
    private TFLHelper tflHelper;

    @Test
    void shortenDestinationName() {
        assertEquals("Test Destination Name", tflHelper.shortenDestinationName("Example for Testing"));
    }

    @Test
    void shortenDestinationNameDefaults() {
        assertEquals("Unknown Station Name", tflHelper.shortenDestinationName("Unknown Station Name"));
    }

    @Test
    void getStopNameFromId() {
        assertEquals("Test Stop Name", tflHelper.getStopNameFromId("TestStop"));
    }

    @Test
    void getStopNameFromIdDefaults() {
        assertEquals("Unknown", tflHelper.getStopNameFromId("ShouldBeUnknown"));
    }

    @Test
    void arrivalMessageShouldBeDueWhenLessThan60Seconds() { assertEquals("Due", tflHelper.getArrivalMessageFromSeconds(59)); }

    @Test
    void arrivalMessageShouldBeSingleMinuteAt60Seconds() { assertEquals("1m", tflHelper.getArrivalMessageFromSeconds(60)); }

    @Test
    void arrivalMessageShouldRoundDownToCompleteWholeMinute() { assertEquals("2m", tflHelper.getArrivalMessageFromSeconds(179)); }

}