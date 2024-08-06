package com.softeer.podoarrival.event.controller;


import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.common.response.ErrorCode;
import com.softeer.podoarrival.event.exception.AsyncRequestExecuteException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseService;
import com.softeer.podoarrival.event.service.ArrivalEventService;
import com.softeer.podoarrival.security.Auth;
import com.softeer.podoarrival.security.AuthInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/arrival")
@Slf4j
public class ArrivalEventController {

    private final ArrivalEventService arrivalEventService;

    @PostMapping("/application")
    @Operation(summary = "선착순 응모용 Api")
    public CompletableFuture<CommonResponse<ArrivalApplicationResponseDto>> arrivalEventApplication(@Auth AuthInfo authInfo) {
        // 비동기 작업을 처리하고 CompletableFuture를 반환
        return arrivalEventService.applyEvent(authInfo)
                .thenApply(result -> new CommonResponse<>(result))
                .exceptionally(ex -> {
                    log.error("비동기 처리 중 오류 발생", ex);
                    // 예외 발생 시 적절한 오류 응답을 반환
                    throw new AsyncRequestExecuteException("선착순 요청 중 서버 오류 발생");
                });
    }

}
