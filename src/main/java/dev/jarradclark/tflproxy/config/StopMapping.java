package dev.jarradclark.tflproxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Used to store the mapping configurations of stopId's to stop names
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "stop-mapping")
public class StopMapping {
    private Map<String,String> stops;
}
