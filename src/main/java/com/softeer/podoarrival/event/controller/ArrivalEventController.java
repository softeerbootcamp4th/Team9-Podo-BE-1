package com.softeer.podoarrival.event.controller;


import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.event.exception.AsyncRequestExecuteException;
import com.softeer.podoarrival.event.exception.ExistingUserException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
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
                    // 내부 예외 처리
                    if(ex.getCause() instanceof ExistingUserException) {
                        throw new ExistingUserException("[비동기 에러] 유저가 이미 존재합니다.");
                    } else {
                        throw new AsyncRequestExecuteException("[비동기 에러] 선착순 요청 중 서버 오류 발생");
                    }
                });
    }
}
