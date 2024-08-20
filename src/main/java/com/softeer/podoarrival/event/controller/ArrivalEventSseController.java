package com.softeer.podoarrival.event.controller;

import com.softeer.podoarrival.event.service.ArrivalEventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/arrival")
@Slf4j
public class ArrivalEventSseController {
	private final ArrivalEventService arrivalEventService;

	@GetMapping(value = "/time", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "선착순 서버시간 SSE Api")
	public Flux<Long> streamServerTime() {
		return arrivalEventService.streamServerTime();
	}
}
