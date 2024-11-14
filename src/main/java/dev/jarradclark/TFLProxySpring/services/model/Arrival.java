package dev.jarradclark.TFLProxySpring.services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Arrival {
    private String lineName;
    private String destinationName;
    private Integer timeToStation;
    private String arrivalMessage;
}

