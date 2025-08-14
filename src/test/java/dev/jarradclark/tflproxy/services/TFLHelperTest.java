package dev.jarradclark.tflproxy.services;

import dev.jarradclark.tflproxy.config.MainProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TFLHelperTest {

    @Autowired
    private TFLHelper tflHelper;

    @Autowired
    private MainProperties properties;

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
    void arrivalMessageShouldBeSingleMinuteJustOver60Seconds() { assertEquals("1m", tflHelper.getArrivalMessageFromSeconds(61)); }

    @Test
    void arrivalMessageShouldRoundDownToCompleteWholeMinute() { assertEquals("2m", tflHelper.getArrivalMessageFromSeconds(179)); }

    @Test
    void getDefaultLineColourWhenNoMappingExists() {
        assertEquals(properties.getDefaultColour(), tflHelper.getLineColourFromLineName("Unknown"));
    }

    @Test
    void getMappedStopColourWhenConfigrationExists() {
        assertEquals("OxF0F0F0", tflHelper.getLineColourFromLineName("TEST"));
    }


}