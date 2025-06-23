package dev.jarradclark.tflproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "destination-mapping")
public record DestinationMapping(Map<String, String> destinations) {}
