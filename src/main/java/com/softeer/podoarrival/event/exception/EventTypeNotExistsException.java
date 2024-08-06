package com.softeer.podoarrival.event.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventTypeNotExistsException extends RuntimeException {
    private String message;
}
