package dev.jarradclark.tflproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Used to store the mapping configurations of stopId's to stop names
 */
@ConfigurationProperties(prefix = "stop-mapping")
public record StopMapping(Map<String,String> stops) {}