package com.softeer.podoarrival.unit.base;

import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventReward;
import com.softeer.podoarrival.event.repository.EventRepository;
import com.softeer.podoarrival.event.repository.EventRewardRepository;
import com.softeer.podoarrival.event.repository.EventTypeRepository;
import com.softeer.podoarrival.event.scheduler.ArrivalEventInformationScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class ArrivalEventInformationBase {

    @Mock
    protected EventRepository eventRepository;

    @Mock
    protected EventRewardRepository eventRewardRepository;

    @Mock
    protected EventTypeRepository eventTypeRepository;

    @InjectMocks
    protected ArrivalEventInformationScheduler arrivalEventInformationScheduler;

    protected Event eventSample;
    protected EventReward eventReward1;
    protected EventReward eventReward2;

    @BeforeEach
    public void setUp() {
        eventSample = Event.builder()
                .id(1L)
                .title("셀토스 선착순 이벤트")
                .description("The 2025 셀토스 출시 기념 선착순 이벤트")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(5))
                .repeatTime(LocalTime.of(15, 0))
                .repeatDay("1111111")
                .build();

        eventReward1 = EventReward.builder()
                .id(1L)
                .event(eventSample)
                .numWinners(10)
                .build();

        eventReward2 = EventReward.builder()
                .id(2L)
                .event(eventSample)
                .numWinners(50)
                .build();
    }

    @AfterEach
    public void tearDown() {
    }
}
