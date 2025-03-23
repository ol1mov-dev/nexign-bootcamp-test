package com.projects.nexigntest.controllers.responses;

import lombok.Builder;

@Builder
public record GenerateCdrReportResponse(
        String msisdn,
        String message
) { }
