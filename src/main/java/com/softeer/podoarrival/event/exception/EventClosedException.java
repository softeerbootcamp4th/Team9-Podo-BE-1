package com.softeer.podoarrival.event.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventClosedException extends RuntimeException {
	private String message;
}
