package dev.jarradclark.tflproxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The apps main configuration properties
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix="main")
public class MainProperties {
    private String env;
    private String defaultStop;
    private int revertToDefaultValue;
    private String revertToDefaultTimeUnit;
    private String apiKey;
}
