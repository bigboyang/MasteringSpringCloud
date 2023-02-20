package com.example.gatewayservice.controller;


import com.example.gatewayservice.service.InternalApiServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalApiController {

    private final InternalApiServiceImpl internalApiService;

    public InternalApiController(InternalApiServiceImpl internalApiService) {
        this.internalApiService = internalApiService;
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refreshRoutingService() {
        internalApiService.refreshRoutingService();
    }

}

