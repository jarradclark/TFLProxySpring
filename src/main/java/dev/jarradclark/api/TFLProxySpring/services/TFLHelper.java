package dev.jarradclark.api.TFLProxySpring.services;

import dev.jarradclark.api.TFLProxySpring.config.DestinationMapping;
import dev.jarradclark.api.TFLProxySpring.config.StopMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TFLHelper {

    private final Map<String,String> stopMap;
    private final Map<String, String> destinationMap;

    @Autowired
    StopMapping stopMapping;

    @Autowired
    DestinationMapping destinationMapping;

    @Autowired
    private TFLHelper(StopMapping stopMap, DestinationMapping destinationMap) {
        this.stopMap = stopMap.getStops();
        this.destinationMap = destinationMap.getDestinations();
    }

    public String shortenDestinationName(String destination) {
        if(destinationMap.containsKey(destination)) {
            return destinationMap.get(destination);
        }
        return destination;
    }

    public  String getStopNameFromId(String stopId) {
        if(stopMap.containsKey(stopId)) {
            return stopMap.get(stopId);
        }
        return "Unknown";
    }

    public String getArrivalMessageFromSeconds(int seconds) {
        if (seconds < 60) {
            return "Due";
        } else {
            return String.format("%sm", Math.round(seconds / 60));
        }
    }

}
