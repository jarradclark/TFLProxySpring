package dev.jarradclark.tflproxy.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DestinationMappingTest {

    @Autowired
    DestinationMapping destinationMap;

    @Test
    void verifyTestDestinationAvailable() {
        assertEquals("Test Destination Name", destinationMap.destinations().get("Example for Testing"));
    }
}