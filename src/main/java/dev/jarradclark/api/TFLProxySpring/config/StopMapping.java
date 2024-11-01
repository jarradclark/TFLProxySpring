package dev.jarradclark.api.TFLProxySpring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "stop-mapping")
public class StopMapping {
    private Map<String,String> stops;
}
