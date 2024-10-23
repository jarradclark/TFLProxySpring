package api.jarradclark.dev.TFLProxySpring.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TFLUtilityTest {

    @Test
    void shortenDestinationName() {
        assertEquals("Test Destination Name", TFLUtility.shortenDestinationName("Example for Testing"));
    }

    @Test
    void shortenDestinationNameDefaults() {
        assertEquals("Unknown Station Name", TFLUtility.shortenDestinationName("Unknown Station Name"));
    }

    @Test
    void getStopNameFromId() {
        assertEquals("Test Stop Name", TFLUtility.getStopNameFromId("TestStop"));
    }

    @Test
    void getStopNameFromIdDefaults() {
        assertEquals("Unknown", TFLUtility.getStopNameFromId("ShouldBeUnknown"));
    }
}