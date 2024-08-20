package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final ArrivalEventReleaseService arrivalEventReleaseServiceRedisImpl;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo) {
        return arrivalEventReleaseServiceRedisImpl.applyEvent(authInfo);
    }

    /**
     * SSE로 서버시간기준 이벤트 시작 시간까지 남은 시간을 전송
     * @return Flux<String> sse로 전송하게되는 이벤트까지 남은 시간
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
