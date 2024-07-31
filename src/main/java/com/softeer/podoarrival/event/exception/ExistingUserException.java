package com.softeer.podoarrival.event.exception;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExistingUserException extends RuntimeException {
    private String message;
}
