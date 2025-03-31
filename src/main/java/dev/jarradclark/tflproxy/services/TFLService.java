package dev.jarradclark.tflproxy.services;


import dev.jarradclark.tflproxy.services.model.ArrivalData;
import dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration;

public interface TFLService {
    ArrivalData getArrivals();
    ArrivalData getArrivalsForStop(String stopId);
    String getCurrentStop();
    void setCurrentStop(String newStop);
    ScheduledResetConfiguration getCurrentScheduledResetConfiguration();
}