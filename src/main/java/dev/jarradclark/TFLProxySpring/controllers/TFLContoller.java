package dev.jarradclark.TFLProxySpring.controllers;

import dev.jarradclark.TFLProxySpring.config.MainProperties;
import dev.jarradclark.TFLProxySpring.services.TFLService;
import dev.jarradclark.TFLProxySpring.services.model.ArrivalData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class TFLContoller {

    private final TFLService tflService;

    @Autowired
    private MainProperties properties;

    public TFLContoller(TFLService tflService) {
        this.tflService = tflService;
    }

    @GetMapping("allArrivals")
    public ResponseEntity<ArrivalData> listArrivals(@RequestHeader HttpHeaders headers) {
        if (isAuthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<ArrivalData>(tflService.getArrivals(), HttpStatus.OK);
    }

    @GetMapping("arrivals/{stopId}")
    public ResponseEntity<ArrivalData> listArrivalsForStop(@RequestHeader HttpHeaders headers, @PathVariable String stopId) {
        if (isAuthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<ArrivalData>(tflService.getArrivalsForStop(stopId), HttpStatus.OK);
    }

    @PostMapping("changeCurrentStop/{stopId}")
    public ResponseEntity<String> changeCurrentStop(@RequestHeader HttpHeaders headers, @PathVariable String stopId) {
        if (isAuthorisedRequest(headers)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        tflService.setCurrentStop(stopId);
        return new ResponseEntity<String>(String.format("Stop Changed to %s", stopId), HttpStatus.ACCEPTED);
    }

    private boolean isAuthorisedRequest(HttpHeaders headers) {
        return !Objects.equals(headers.getFirst("API-Key"), properties.getApiKey());
    }
}
