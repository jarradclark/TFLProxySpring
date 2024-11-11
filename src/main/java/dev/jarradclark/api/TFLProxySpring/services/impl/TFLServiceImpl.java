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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TFLServiceImpl implements TFLService {

    private final TFLClient tflClient;
    private final MainProperties properties;
    private static final Logger logger = LoggerFactory.getLogger(TFLServiceImpl.class);
    private final ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledResetTask;
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
        List<Arrival> arrivalList = reformatArrivalList(tflClient.getArrivalsForStop(stopId));
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
        scheduleDefaultStopReset();
    }

    private List<Arrival> reformatArrivalList(List<Arrival> arrivalList) {
        arrivalList.forEach(arrival -> {
           arrival.setDestinationName(tflHelper.shortenDestinationName(arrival.getDestinationName()));
           arrival.setArrivalMessage(tflHelper.getArrivalMessageFromSeconds(arrival.getTimeToStation()));
        });
        return arrivalList;
    }

    private void scheduleDefaultStopReset() {
        if (this.scheduledResetTask != null && !this.scheduledResetTask.isDone()) {
            this.scheduledResetTask.cancel(true);
            logger.debug("Canceling existing scheduled task");
        }

        Runnable scheduledTask = () -> {
            this.currentStop = this.defaultStop;
            logger.info("Scheduled reset of current stop back to default [{}]",this.defaultStop);
        };
        logger.debug("Service will reset to default in {} {}",properties.getRevertToDefaultValue(),properties.getRevertToDefaultTimeUnit());
        this.scheduledResetTask = this.taskScheduler.schedule(scheduledTask, properties.getRevertToDefaultValue(), TimeUnit.valueOf(properties.getRevertToDefaultTimeUnit()));

    }
}

