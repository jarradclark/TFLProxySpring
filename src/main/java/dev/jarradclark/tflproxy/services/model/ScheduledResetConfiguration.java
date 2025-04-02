package dev.jarradclark.tflproxy.services.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class ScheduledResetConfiguration {

    private int value;

    @NotNull(message = "A valid time unit is required")
    @NotBlank(message = "A valid time unit is required")
    private String timeUnit;
}
