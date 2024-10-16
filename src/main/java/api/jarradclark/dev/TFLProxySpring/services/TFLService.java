package api.jarradclark.dev.TFLProxySpring.services;


import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;

import java.util.List;

public interface TFLService {

    List<Arrival> getArrivals();
    List<Arrival> getArrivalsForStop(String stopId);
    String getCurrentStop();
    void setCurrentStop(String newStop);


}