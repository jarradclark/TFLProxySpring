package dev.jarradclark.tflproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "colour-mapping")
public record ColourMapping(Map<String,String> lines) {}
