package dev.jarradclark.api.TFLProxySpring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "destination-mapping")
public class DestinationMapping {
    private Map<String, String> destinations;
}
