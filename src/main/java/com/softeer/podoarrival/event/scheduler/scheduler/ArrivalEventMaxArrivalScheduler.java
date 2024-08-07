package com.softeer.podoarrival.event.scheduler.scheduler;

import com.softeer.podoarrival.event.exception.EventTypeNotExistsException;
import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventType;
import com.softeer.podoarrival.event.repository.EventRepository;
import com.softeer.podoarrival.event.repository.EventRewardRepository;
import com.softeer.podoarrival.event.repository.EventTypeRepository;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 선착순 이벤트의 당첨자 수를 세팅하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArrivalEventMaxArrivalScheduler {

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventRewardRepository eventRewardRepository;

    /**
     * 특정 시간에 Mysql에서 금일 진행될 선착순 이벤트의 당첨자 수를 읽어옴
     *
     */
    @Scheduled(cron = "0 25 03 * * *")
    public void setDailyQuiz() {
        // 시작일자, 이벤트 종류만 고려하여 이벤트 추출
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        // 선착순 이벤트
        EventType eventType = eventTypeRepository.findById(1L).orElseThrow(() -> new EventTypeNotExistsException("이벤트 타입이 존재하지 않습니다."));
        Event findEvent = eventRepository.findFirstByEventTypeAndStartAtBetween(eventType, startOfDay, endOfDay);

        // 찾은 이벤트에 해당하는 reword개수 조회
        int rewordCount = eventRewardRepository.countByEvent(findEvent);

        ArrivalEventReleaseService.setMaxArrival(rewordCount);
    }
}
