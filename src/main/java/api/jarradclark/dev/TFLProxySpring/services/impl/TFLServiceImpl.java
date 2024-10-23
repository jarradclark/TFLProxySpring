package api.jarradclark.dev.TFLProxySpring.services.impl;

import api.jarradclark.dev.TFLProxySpring.services.TFLUtility;
import api.jarradclark.dev.TFLProxySpring.services.model.Arrival;
import api.jarradclark.dev.TFLProxySpring.services.model.ArrivalComparator;
import api.jarradclark.dev.TFLProxySpring.services.model.ArrivalData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import api.jarradclark.dev.TFLProxySpring.services.TFLClient;
import api.jarradclark.dev.TFLProxySpring.services.TFLService;

import java.util.List;

@Service
public class TFLServiceImpl implements TFLService {

    private final TFLClient tflClient;
    private static final Logger logger = LoggerFactory.getLogger(TFLServiceImpl.class);

    @Value("${TFLService.defaultStop}")
    public String currentStop;

    public TFLServiceImpl(final TFLClient tflClient) {
        this.tflClient = tflClient;
    }

    @Override
    public ArrivalData getArrivals() {
        return getArrivalsForStop(currentStop);
    }

    @Override
    public ArrivalData getArrivalsForStop(String stopId) {
        String stopName = TFLUtility.getStopNameFromId(stopId);
        logger.info("Getting arrivals for stop [{}] [{}]", stopId, stopName);
        List<Arrival> arrivalList = shortenDestinations(tflClient.getArrivalsForStop(stopId));
        arrivalList.sort(new ArrivalComparator());
        return ArrivalData.builder()
                .arrivalList(arrivalList)
                .currentStop(stopId)
                .stopName(stopName)
                .build();
    }

    @Override
    public String getCurrentStop() {
        return currentStop;
    }

    @Override
    public void setCurrentStop(String newStop) {
        currentStop = newStop;
        logger.info("TFL Service set to new stop [{}] [{}]", newStop, TFLUtility.getStopNameFromId(newStop));
    }

    private List<Arrival> shortenDestinations(List<Arrival> arrivalList) {
        arrivalList.forEach(arrival -> arrival.setDestinationName(TFLUtility.shortenDestinationName(arrival.getDestinationName())));
        return arrivalList;
    }
}
