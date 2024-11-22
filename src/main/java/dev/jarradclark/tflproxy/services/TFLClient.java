package dev.jarradclark.tflproxy.services;

import dev.jarradclark.tflproxy.services.model.Arrival;

import java.util.List;

public interface TFLClient {
    List<Arrival> getArrivalsForStop(String stopId);
}
