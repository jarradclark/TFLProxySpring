package dev.jarradclark.TFLProxySpring.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MainPropertiesTest {

    @Autowired
    MainProperties properties;

    @Test
    void getDefaultStop() {
        assertEquals("490009538S", properties.getDefaultStop());
    }
}