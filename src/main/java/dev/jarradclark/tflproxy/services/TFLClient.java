package dev.jarradclark.tflproxy.services;

import dev.jarradclark.tflproxy.services.model.Arrival;

import java.util.List;

/**
 * The interface that describes the methods available from the TFL Client
 */
public interface TFLClient {

    /**
     * This method returns a list of arrivals for the requested stopId retrieved from TFL
     * @param stopId the id of the stop requested
     * @return a List of Arrival objects
     */
    List<Arrival> getArrivalsForStop(String stopId);
}
