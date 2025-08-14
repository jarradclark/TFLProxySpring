package dev.jarradclark.tflproxy.services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Arrival object that contains all relevant arrival data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Arrival {

    /**
     * The name of the line
     */
    private String lineName;
    /**
     * The human-readable destination
     */
    private String destinationName;
    /**
     * The number of seconds until the bus is expected to arrive
     */
    private Integer timeToStation;
    /**
     * The human-readable message used for display based on the timeToStation
     */
    private String arrivalMessage;

    /**
     * The colour mapped for the selected line if available (defaults to configured color if not mapping found)
     */
    private String colour;
}

