package dev.jarradclark.tflproxy.services.impl;

import dev.jarradclark.tflproxy.services.model.Arrival;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dev.jarradclark.tflproxy.services.TFLClient;

import java.util.List;

@Service
public class TFLClientImpl implements TFLClient {

    private static final Logger logger = LoggerFactory.getLogger(TFLClientImpl.class);
    private final RestTemplate restTemplate;

    public TFLClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Arrival> getArrivalsForStop(String stopId) {
        final ParameterizedTypeReference<List<Arrival>> arrivalsList = new ParameterizedTypeReference<>() {
        } ;

        try {
            final ResponseEntity<List<Arrival>> response = restTemplate.exchange(
                    "https://api.tfl.gov.uk/StopPoint/{stopId}/arrivals", HttpMethod.GET, null, arrivalsList, stopId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error(response.getBody().toString());
                throw new RestClientException("Non 200 response from API");
            }

            return response.getBody();
        } catch (RestClientException exception) {
            logger.error("Rest Client had an error when retrieving data from TFL",exception);
            throw exception;
        }
    }
}
