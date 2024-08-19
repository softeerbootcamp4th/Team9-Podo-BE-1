package com.softeer.podoarrival.event.scheduler;

import com.softeer.podoarrival.event.exception.EventTypeNotExistsException;
import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventReward;
import com.softeer.podoarrival.event.model.entity.EventType;
import com.softeer.podoarrival.event.repository.EventRepository;
import com.softeer.podoarrival.event.repository.EventRewardRepository;
import com.softeer.podoarrival.event.repository.EventTypeRepository;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceJavaImpl;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceRedisImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 선착순 이벤트의 당첨자 수를 세팅하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArrivalEventInformationScheduler {

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventRewardRepository eventRewardRepository;

    /**
     * 특정 시간에 Mysql에서 금일 진행될 선착순 이벤트의 절보를 세팅함.
     * 당첨자 수, 이벤트 시작 시간, 이벤트 요일 설정
     *
     */
    @Scheduled(cron = "0 25 03 * * *")
    public void setArrivalEventInformation() {
        // 시작일자, 이벤트 종류만 고려하여 이벤트 추출
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        // 선착순 이벤트
        EventType eventType = eventTypeRepository.findById(1L).orElseThrow(() -> new EventTypeNotExistsException("이벤트 타입이 존재하지 않습니다."));
        Event findEvent = eventRepository.findFirstByEventTypeAndStartAtBetween(eventType, startOfDay, endOfDay);

        if(findEvent == null) {
            log.warn("오늘 날짜에 이벤트가 없습니다.");
            ArrivalEventReleaseServiceRedisImpl.setMaxArrival(0);
            ArrivalEventReleaseServiceJavaImpl.setMaxArrival(0);
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
            ArrivalEventReleaseServiceRedisImpl.setStartDate(true);
            ArrivalEventReleaseServiceJavaImpl.setStartDate(true);
        }else{
            ArrivalEventReleaseServiceRedisImpl.setStartDate(false);
            ArrivalEventReleaseServiceJavaImpl.setStartDate(false);
        }

        // service에 이벤트 내용 저장
        ArrivalEventReleaseServiceRedisImpl.setMaxArrival(rewardCount);
        ArrivalEventReleaseServiceJavaImpl.setMaxArrival(rewardCount);

        ArrivalEventReleaseServiceRedisImpl.setStartTime(repeatTime);
        ArrivalEventReleaseServiceJavaImpl.setStartTime(repeatTime);
    }
}
