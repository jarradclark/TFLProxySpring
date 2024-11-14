package dev.jarradclark.TFLProxySpring.services;

import dev.jarradclark.TFLProxySpring.services.model.Arrival;

import java.util.List;

public interface TFLClient {
    List<Arrival> getArrivalsForStop(String stopId);
}
