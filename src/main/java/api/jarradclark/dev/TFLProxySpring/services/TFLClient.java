package api.jarradclark.dev.TFLProxySpring.services;

import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;

import java.util.List;

public interface TFLClient {
    List<Arrival> getArrivalsForStop(String stopId);
}
