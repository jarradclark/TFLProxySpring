package dev.jarradclark.api.TFLProxySpring.services.impl;

import dev.jarradclark.api.TFLProxySpring.config.MainProperties;
import dev.jarradclark.api.TFLProxySpring.services.TFLHelper;
import dev.jarradclark.api.TFLProxySpring.services.model.Arrival;
import dev.jarradclark.api.TFLProxySpring.services.model.ArrivalComparator;
import dev.jarradclark.api.TFLProxySpring.services.model.ArrivalData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.jarradclark.api.TFLProxySpring.services.TFLClient;
import dev.jarradclark.api.TFLProxySpring.services.TFLService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TFLServiceImpl implements TFLService {

    private final TFLClient tflClient;
    private final MainProperties properties;
    private static final Logger logger = LoggerFactory.getLogger(TFLServiceImpl.class);
    private final ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor();
    private final String defaultStop;
    public String currentStop;

    @Autowired
    private TFLHelper tflHelper;

    @Autowired
    public TFLServiceImpl(final TFLClient tflClient, MainProperties properties) {
        this.tflClient = tflClient;
        this.properties = properties;
        this.defaultStop = properties.getDefaultStop();
        this.currentStop = this.defaultStop;
    }

    @Override
    public ArrivalData getArrivals() {
        return getArrivalsForStop(currentStop);
    }

    @Override
    public ArrivalData getArrivalsForStop(String stopId) {
        String stopName = tflHelper.getStopNameFromId(stopId);
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
        logger.info("TFL Service set to new stop [{}] [{}]", newStop, tflHelper.getStopNameFromId(newStop));

        Runnable testTask = () -> {
            this.currentStop = this.defaultStop;
            logger.info("Reset Current stop to default [{}]",this.defaultStop);
        };
        logger.debug("Service will reset to default in {} {}",properties.getRevertToDefaultValue(),properties.getRevertToDefaultTimeUnit());
        this.taskScheduler.schedule(testTask, properties.getRevertToDefaultValue(), TimeUnit.valueOf(properties.getRevertToDefaultTimeUnit()));
    }

    private List<Arrival> shortenDestinations(List<Arrival> arrivalList) {
        arrivalList.forEach(arrival -> arrival.setDestinationName(tflHelper.shortenDestinationName(arrival.getDestinationName())));
        return arrivalList;
    }
}
