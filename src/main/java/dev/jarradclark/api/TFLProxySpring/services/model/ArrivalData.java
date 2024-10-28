package dev.jarradclark.api.TFLProxySpring.services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArrivalData {
    private String currentStop;
    private String stopName;
    private List<Arrival> arrivalList;
}
