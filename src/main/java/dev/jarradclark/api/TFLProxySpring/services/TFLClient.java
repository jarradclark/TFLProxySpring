package dev.jarradclark.api.TFLProxySpring.services;

import dev.jarradclark.api.TFLProxySpring.services.model.Arrival;

import java.util.List;

public interface TFLClient {
    List<Arrival> getArrivalsForStop(String stopId);
}
