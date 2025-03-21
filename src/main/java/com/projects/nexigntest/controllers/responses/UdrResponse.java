package com.projects.nexigntest.controllers.responses;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UdrResponse(
    String msisdn,
    Call incomingCall,
    Call outcomingCall
) {
    @Builder
    public record Call(String total){}
}