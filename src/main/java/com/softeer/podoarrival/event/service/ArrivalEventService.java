package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.event.exception.AsyncRequestExecuteException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final ExecutorService executorService;
    private final ArrivalEventReleaseService arrivalEventReleaseService;

    public CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo) {
        return arrivalEventReleaseService.applyEvent(authInfo);
    }
}
