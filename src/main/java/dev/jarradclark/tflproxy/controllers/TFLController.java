package dev.jarradclark.tflproxy.controllers;

import dev.jarradclark.tflproxy.config.MainProperties;
import dev.jarradclark.tflproxy.services.TFLService;
import dev.jarradclark.tflproxy.services.model.ArrivalData;
import dev.jarradclark.tflproxy.services.model.ScheduledResetConfiguration;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class TFLController {

    private final TFLService tflService;

    private static final Logger logger = LoggerFactory.getLogger(TFLController.class);

    @Autowired
    private MainProperties properties;

    public TFLController(TFLService tflService) {
        this.tflService = tflService;
    }

    @GetMapping("allArrivals")
    public ResponseEntity<ArrivalData> listArrivals(@RequestHeader HttpHeaders headers) {
        if (isUnauthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(tflService.getArrivals(), HttpStatus.OK);
    }

    @GetMapping("arrivals/{stopId}")
    public ResponseEntity<ArrivalData> listArrivalsForStop(@RequestHeader HttpHeaders headers, @PathVariable String stopId) {
        if (isUnauthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(tflService.getArrivalsForStop(stopId), HttpStatus.OK);
    }

    @PostMapping("changeCurrentStop/{stopId}")
    public ResponseEntity<String> changeCurrentStop(@RequestHeader HttpHeaders headers, @PathVariable String stopId) {
        if (isUnauthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        tflService.setCurrentStop(stopId);
        return new ResponseEntity<>(String.format("Stop Changed to %s", stopId), HttpStatus.ACCEPTED);
    }

    @GetMapping("currentScheduledResetConfiguration")
    public ResponseEntity<ScheduledResetConfiguration> currentScheduledResetConfiguration(@RequestHeader HttpHeaders headers) {
        if (isUnauthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(tflService.getScheduledResetConfiguration(), HttpStatus.OK);
    }

    @PatchMapping("setScheduledResetConfiguration")
    public ResponseEntity<ScheduledResetConfiguration> setScheduledResetConfiguration(@RequestHeader HttpHeaders headers,@Valid @RequestBody ScheduledResetConfiguration resetConfiguration) {
        if (isUnauthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            ScheduledResetConfiguration newConfiguration = tflService.setScheduledResetConfiguration(resetConfiguration.getValue(), resetConfiguration.getTimeUnit());
            return new ResponseEntity<>(newConfiguration, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    private boolean isUnauthorisedRequest(HttpHeaders headers) {
        boolean isMissingOrInvalidKey = !Objects.equals(headers.getFirst("API-Key"), properties.getApiKey());
        if (isMissingOrInvalidKey) logger.warn("Request made with invalid API-Key. Current key ends with {}", properties.getApiKey().substring(properties.getApiKey().length() - 3));
        return isMissingOrInvalidKey;
    }
}
