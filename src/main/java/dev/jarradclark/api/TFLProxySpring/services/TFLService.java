package dev.jarradclark.api.TFLProxySpring.services;


import dev.jarradclark.api.TFLProxySpring.services.model.ArrivalData;

public interface TFLService {
    ArrivalData getArrivals();
    ArrivalData getArrivalsForStop(String stopId);
    String getCurrentStop();
    void setCurrentStop(String newStop);
}