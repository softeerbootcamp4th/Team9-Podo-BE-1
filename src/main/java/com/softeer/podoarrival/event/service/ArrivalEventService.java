package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final ArrivalEventReleaseService arrivalEventReleaseServiceRedisImpl;

    public CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo) {
        return arrivalEventReleaseServiceRedisImpl.applyEvent(authInfo);
    }
}
