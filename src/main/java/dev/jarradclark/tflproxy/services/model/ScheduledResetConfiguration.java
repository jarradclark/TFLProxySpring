package dev.jarradclark.tflproxy.services.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class ScheduledResetConfiguration {

    @Valid

    @Min(value=1, message = "A valid value > 0 is required")
    private int value;

    @NotNull(message = "A valid time unit is required")
    @NotBlank(message = "A valid time unit is required")
    private String timeUnit;
}
