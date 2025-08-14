package dev.jarradclark.tflproxy.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ColourMappingTest {

    @Autowired
    ColourMapping colourMapping;

    @Test
    void testColourMappingIsAvailable() {
        assertEquals("OxF0F0F0", colourMapping.lines().get("TEST"));
    }

}