package api.jarradclark.dev.TFLProxySpring.controllers;

import api.jarradclark.dev.TFLProxySpring.services.TFLService;
import api.jarradclark.dev.TFLProxySpring.services.model.ArrivalData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TFLContoller {

    private final TFLService tflService;

    public TFLContoller(TFLService tflService) {
        this.tflService = tflService;
    }

    @GetMapping("allArrivals")
    public ResponseEntity<ArrivalData> listArrivals() {
        return new ResponseEntity<ArrivalData>(tflService.getArrivals(), HttpStatus.OK);
    }

    @GetMapping("arrivals/{stopId}")
    public ResponseEntity<ArrivalData> listArrivalsForStop(@PathVariable String stopId) {
        return new ResponseEntity<ArrivalData>(tflService.getArrivalsForStop(stopId), HttpStatus.OK);
    }

    @PostMapping("changeCurrentStop/{stopId}")
    public ResponseEntity<String> changeCurrentStop(@PathVariable String stopId) {
        tflService.setCurrentStop(stopId);
        return new ResponseEntity<String>(String.format("Stop Changed to %s", stopId), HttpStatus.ACCEPTED);
    }
}
