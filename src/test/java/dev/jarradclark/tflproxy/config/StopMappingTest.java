package dev.jarradclark.tflproxy.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StopMappingTest {

    @Autowired
    StopMapping stopMapping;

    @Test
    void verifyTestStopIsAvailable() {
        assertEquals("Test Stop Name", stopMapping.stops().get("TestStop"));
    }
}