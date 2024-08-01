package com.softeer.podoarrival.event.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DailyQuizNotExistsException extends RuntimeException {
    private String message;
}
