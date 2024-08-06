package com.softeer.podoarrival.event.repository;


import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * 이벤트 타입이 일치하고, startAt이 오늘인 이벤트 1개를 반환하는 메서드
     * @param eventType 이벤트 타입
     * @return 조건에 맞는 이벤트 (없으면 null)
     */
    Event findFirstByEventTypeAndStartAtBetween(EventType eventType, LocalDateTime startAtStart, LocalDateTime startAtEnd);
}
