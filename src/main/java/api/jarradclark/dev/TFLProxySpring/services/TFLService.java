package api.jarradclark.dev.TFLProxySpring.services;


import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;
import api.jarradclark.dev.TFLProxySpring.services.model.ArrivalData;

import java.util.List;

public interface TFLService {

    ArrivalData getArrivals();
    ArrivalData getArrivalsForStop(String stopId);
    String getCurrentStop();
    void setCurrentStop(String newStop);


}