package api.jarradclark.dev.TFLProxySpring.services.impl;

import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;
import org.springframework.stereotype.Service;
import api.jarradclark.dev.TFLProxySpring.services.TFLClient;
import api.jarradclark.dev.TFLProxySpring.services.TFLService;

import java.util.List;

@Service
public class TFLServiceImpl implements TFLService {

    private final TFLClient tflClient;
    public String currentStop = "490009538N";

    public TFLServiceImpl(final TFLClient tflClient) {
        this.tflClient = tflClient;
    }

    @Override
    public List<Arrival> getArrivals() {
        return getArrivalsForStop(currentStop);
    }

    @Override
    public List<Arrival> getArrivalsForStop(String stopId) {
        List<Arrival> arrivalList = tflClient.getArrivalsForStop(stopId);
        arrivalList.sort(new ArrivalComparator());

        return arrivalList;
    }

    @Override
    public String getCurrentStop() {
        return currentStop;
    }

    @Override
    public void setCurrentStop(String newStop) {
        currentStop = newStop;
    }
}

class ArrivalComparator implements java.util.Comparator<Arrival> {
    @Override
    public int compare(Arrival o1, Arrival o2) {
        return o1.getTimeToStation() - o2.getTimeToStation();
    }
}
