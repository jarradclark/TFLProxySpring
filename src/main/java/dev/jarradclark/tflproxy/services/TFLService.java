package dev.jarradclark.tflproxy.services;


import dev.jarradclark.tflproxy.services.model.ArrivalData;
import dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration;

public interface TFLService {
    /**
     * Get all bus arrivals for the currently configured stop
     * @return An ArrivalData object see {@link dev.jarradclark.tflproxy.services.model.ArrivalData} for details
     */
    ArrivalData getArrivals();

    /**
     * Get all bus arrivals for the requested stop
     * @param stopId The stop id you want the arrival data from
     * @return An ArrivalData object see {@link dev.jarradclark.tflproxy.services.model.ArrivalData} for details
     */
    ArrivalData getArrivalsForStop(String stopId);

    /**
     * Get the currently configured stop
     * @return the stopId of the currently configured stop
     */
    String getCurrentStop();

    /**
     * Change the currently configured stop to a new value
     * @param newStop The stopId you want to change the configured stop to
     */
    void setCurrentStop(String newStop);

    /**
     * Get the currently scheduled reset configuration
     * @return The current configuration as a ScheduledResetConfiguration object see {@link dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration} for details
     */
    ScheduledResetConfiguration getScheduledResetConfiguration();

    /**
     * Set the scheduled reset configuration to a new set of values
     * @param value The number of time units you with to configure
     * @param timeUnit The unit of time to use configure see {@link java.util.concurrent.TimeUnit } for types
     * @return The new configuration as a ScheduledResetConfiguration object see {@link dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration} for details
     */
    ScheduledResetConfiguration setScheduledResetConfiguration(int value, String timeUnit);
}