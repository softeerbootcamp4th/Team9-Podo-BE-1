package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final ArrivalEventReleaseService arrivalEventReleaseServiceRedisImpl;

    /**
     * 선착순 응모용 service.
     * arrivalEventReleaseServiceJavaImpl과 arrivalEventReleaseServiceRedisImpl을 ArrivalEventReleaseService에 갈아끼우면 해당 방식으로 작동하게 된다.
     * @param authInfo 사용자 jwt 토큰 정보
     * @return 선착순 응모 async method에 대한 CompletableFuture 타입 반환
     */
    public CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo) {
        return arrivalEventReleaseServiceRedisImpl.applyEvent(authInfo);
    }

    /**
     * SSE로 서버시간기준 이벤트 시작 시간까지 남은 시간을 전송
     * @return sse로 전송하게되는 이벤트까지 남은 시간
     */
    public Flux<Long> streamServerTime() {
        return Flux.concat(
                Flux.just(0L), // Emit initial value immediately
                Flux.interval(Duration.ofSeconds(20)))
                .map(sequence -> {
                    LocalTime startTime = ArrivalEventReleaseServiceJavaImpl.getStartTime();
                    long seconds = Duration.between(LocalTime.now(), startTime).getSeconds();
                    return Math.max(seconds, 0);
                });
    }
}
