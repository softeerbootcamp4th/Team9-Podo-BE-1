package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.exception.EventTypeNotExistsException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventReward;
import com.softeer.podoarrival.event.model.entity.EventType;
import com.softeer.podoarrival.event.repository.EventRepository;
import com.softeer.podoarrival.event.repository.EventRewardRepository;
import com.softeer.podoarrival.event.repository.EventTypeRepository;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final ArrivalEventReleaseService arrivalEventReleaseServiceRedisImpl;
    private final EventTypeRepository eventTypeRepository;
    private final EventRewardRepository eventRewardRepository;
    private final EventRepository eventRepository;

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
    public Flux<Long> streamLeftSecondsToEventTime() {
        return Flux.concat(
                Flux.just(0L), // Emit initial value immediately
                Flux.interval(Duration.ofSeconds(20)))
                .map(sequence -> {
                    LocalDateTime startTime = arrivalEventReleaseServiceRedisImpl.getStartTime();
                    long seconds = Duration.between(LocalDateTime.now(), startTime).getSeconds();
                    return Math.max(seconds, 0);
                });
    }

    @Scheduled(cron = "0 25 03 * * *")
    public void setArrivalEventInformation() {
        // Check Flag 초기화
        arrivalEventReleaseServiceRedisImpl.setCheckFlag(false);

        // START DATE, START TIME 초기화
        arrivalEventReleaseServiceRedisImpl.setStartDate(true);
        arrivalEventReleaseServiceRedisImpl.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)));

        // 시작일자, 이벤트 종류만 고려하여 이벤트 추출
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        // 선착순 이벤트
        EventType eventType = eventTypeRepository.findById(1L).orElseThrow(() -> new EventTypeNotExistsException("이벤트 타입이 존재하지 않습니다."));
        Event findEvent = eventRepository.findFirstByEventTypeAndStartAtBetween(eventType, startOfDay, endOfDay);

        if(findEvent == null) {
            log.warn("오늘 날짜에 이벤트가 없습니다.");
            arrivalEventReleaseServiceRedisImpl.setMaxArrival(0);
            return;
        }

        // 찾은 이벤트에 해당하는 reward개수 조회
        int rewardCount = 0;
        List<EventReward> eventRewards = eventRewardRepository.findAllByEvent(findEvent);
        for (EventReward eventReward : eventRewards) {
            rewardCount += eventReward.getNumWinners();
        }

        // 찾은 이벤트에 해당하는 반복 시간 확인
        LocalTime repeatTime = findEvent.getRepeatTime();

        // 찾은 이벤트에 해당하는 반복 요일 확인 및 저장
        String repeatDate = findEvent.getRepeatDay();
        int today = startOfDay.getDayOfWeek().getValue();
        if(repeatDate.length() >= 7 && repeatDate.charAt(today - 1) == '1') {
            arrivalEventReleaseServiceRedisImpl.setStartDate(true);
        }else{
            arrivalEventReleaseServiceRedisImpl.setStartDate(false);
        }

        // service에 이벤트 내용 저장
        arrivalEventReleaseServiceRedisImpl.setMaxArrival(rewardCount);

        arrivalEventReleaseServiceRedisImpl.setStartTime(LocalDateTime.of(LocalDate.now(), repeatTime));
    }
}
