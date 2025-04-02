package dev.jarradclark.tflproxy.services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledResetConfiguration {
    private int value;
    private String timeUnit;
}
