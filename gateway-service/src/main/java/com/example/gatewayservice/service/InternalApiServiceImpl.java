package com.example.gatewayservice.service;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;

public class InternalApiServiceImpl {

    private final ApplicationEventPublisher applicationEventPublisher;

    public InternalApiServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    public void refreshRoutingService() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }


}
