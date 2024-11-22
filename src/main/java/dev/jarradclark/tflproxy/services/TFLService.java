package dev.jarradclark.tflproxy.services;


import dev.jarradclark.tflproxy.services.model.ArrivalData;

public interface TFLService {
    ArrivalData getArrivals();
    ArrivalData getArrivalsForStop(String stopId);
    String getCurrentStop();
    void setCurrentStop(String newStop);
}