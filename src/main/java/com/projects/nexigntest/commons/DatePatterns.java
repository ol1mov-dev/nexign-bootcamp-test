package com.projects.nexigntest.commons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DatePatterns {
    CALL_TOTAL_TIME("%02d:%02d:%02d");

    private final String pattern;
}
