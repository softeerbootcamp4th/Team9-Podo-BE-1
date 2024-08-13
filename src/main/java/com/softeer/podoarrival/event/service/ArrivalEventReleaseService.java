package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;

import java.util.concurrent.CompletableFuture;

public interface ArrivalEventReleaseService {

    CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo);
}
