package dev.jarradclark.tflproxy.services;

import dev.jarradclark.tflproxy.config.ColourMapping;
import dev.jarradclark.tflproxy.config.DestinationMapping;
import dev.jarradclark.tflproxy.config.MainProperties;
import dev.jarradclark.tflproxy.config.StopMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The utility methods/functions designed to help with TFL related data
 */
@Component
public class TFLHelper {

    private final Map<String,String> stopMap;
    private final Map<String, String> destinationMap;
    private final Map<String, String> colourMapping;

    private TFLHelper(StopMapping stopMap, DestinationMapping destinationMap, StopMapping stopMapping, ColourMapping colourMapping) {
        this.destinationMap = destinationMap.destinations();
        this.stopMap = stopMap.stops();
        this.colourMapping = colourMapping.lines();
    }

    @Autowired
    private MainProperties properties;

    /**
     * Shorten the names of destinations based on the configured short descriptions
     * @param destination The original destination string you want shortened
     * @return The shortened destination string
     */
    public String shortenDestinationName(String destination) {
        if(destinationMap.containsKey(destination)) {
            return destinationMap.get(destination);
        }
        return destination;
    }

    /**
     * Get the human-readable stop name from the stopId based on stops stored in the local configuration
     * @param stopId The stopId you are requested the name from
     * @return The human-readable name mapped in the local configuration (Unknown will be returned if no name mapped)
     */
    public  String getStopNameFromId(String stopId) {
        if(stopMap.containsKey(stopId)) {
            return stopMap.get(stopId);
        }
        return "Unknown";
    }

    /**
     * Get an arrival message based on the number of seconds provided
     * @param seconds The number of seconds you wish to be formatted
     * @return An arrival message string for the supplied number of seconds
     */
    public String getArrivalMessageFromSeconds(int seconds) {
        if (seconds < 60) {
            return "Due";
        } else {
            return String.format("%sm", Math.round((float) (seconds / 60)));
        }
    }

    public String getLineColourFromLineName(String lineName) {
        if(colourMapping.containsKey(lineName)) { return colourMapping.get(lineName); }
        return properties.getDefaultColour();
    }

}
