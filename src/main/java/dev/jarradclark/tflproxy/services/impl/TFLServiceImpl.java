package dev.jarradclark.tflproxy.services.impl;

import dev.jarradclark.tflproxy.config.MainProperties;
import dev.jarradclark.tflproxy.services.TFLHelper;
import dev.jarradclark.tflproxy.services.model.Arrival;
import dev.jarradclark.tflproxy.services.model.ArrivalData;
import dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.jarradclark.tflproxy.services.TFLClient;
import dev.jarradclark.tflproxy.services.TFLService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TFLServiceImpl implements TFLService {

    private final TFLClient tflClient;
    private final MainProperties properties;
    private static final Logger logger = LoggerFactory.getLogger(TFLServiceImpl.class);
    private final ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledResetTask;
    private final String defaultStop;
    private String currentStop;

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

    @Override
    public ScheduledResetConfiguration getScheduledResetConfiguration() {
        return ScheduledResetConfiguration.builder()
                .timeUnit(properties.getRevertToDefaultTimeUnit())
                .value(properties.getRevertToDefaultValue())
                .build();
    }

    @Override
    public void setScheduledResetConfiguration(int value, String timeUnit) {
        if(Arrays.stream(TimeUnit.values()).noneMatch(unit -> unit.name().equalsIgnoreCase(timeUnit))) {
            logger.error("setScheduledResetConfiguration received [{}] which is not a valid Time Unit",timeUnit);
            throw new IllegalArgumentException("Invalid Time Unit");
        }
        if(value < 1) {
            logger.error("setScheduledResetConfiguration received a value of [{}] which is not a interval",timeUnit);
            throw new IllegalArgumentException("Value must be a positive value");
        }

        properties.setRevertToDefaultValue(value);
        properties.setRevertToDefaultTimeUnit(TimeUnit.valueOf(timeUnit.toUpperCase()).name());

        logger.info("Scheduled revert updated to reset after [{}] [{}]", value, timeUnit);
    }

    private List<Arrival> reformatArrivalList(List<Arrival> arrivalList) {
        return arrivalList.stream()
                .sorted(Comparator.comparing(Arrival::getTimeToStation))
                .map(arrival ->
                        new Arrival( arrival.getLineName(),
                                tflHelper.shortenDestinationName(arrival.getDestinationName()),
                                arrival.getTimeToStation(),
                                tflHelper.getArrivalMessageFromSeconds(arrival.getTimeToStation())))
                .collect(Collectors.toList());
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

