package com.softeer.podoarrival.unit.scheduler;

import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventType;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseService;
import com.softeer.podoarrival.unit.base.ArrivalEventMaxCountSchedulerBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ArrivalEventMaxCountSchedulerTest extends ArrivalEventMaxCountSchedulerBase {

    @Test
    @DisplayName("이벤트 최대인원 세팅 스케줄러 동작 테스트 (성공 - Mysql에 데이터가 존재하는 경우)")
    public void setArrivalEventCountSuccess_Data_Exists() {
        // given
        when(eventTypeRepository.findById(1L))
                .thenReturn(Optional.ofNullable(EventType.builder()
                        .id(1L)
                        .type("arrival")
                        .build())
                );
        when(eventRepository.findFirstByEventTypeAndStartAtBetween(any(), any(), any()))
                .thenReturn(Event.builder()
                        .build()
                );
        when(eventRewardRepository.countByEvent(any()))
                .thenReturn(50);

        // when
        arrivalEventMaxArrivalScheduler.setEventArrivalCount();

        // then
        Assertions.assertThat(ArrivalEventReleaseService.getMaxArrival())
                .isEqualTo(50);
    }

    @Test
    @DisplayName("이벤트 최대인원 세팅 스케줄러 동작 테스트 (성공 - Mysql에 데이터가 존재하지 않는 경우)")
    public void setArrivalEventCountSuccess_Data_Not_Exists() {
        // given
        when(eventTypeRepository.findById(1L))
                .thenReturn(Optional.ofNullable(EventType.builder()
                        .id(1L)
                        .type("arrival")
                        .build())
                );
        when(eventRepository.findFirstByEventTypeAndStartAtBetween(any(), any(), any()))
                .thenReturn(null);
        when(eventRewardRepository.countByEvent(any()))
                .thenReturn(0);

        // when
        arrivalEventMaxArrivalScheduler.setEventArrivalCount();

        // then
        Assertions.assertThat(ArrivalEventReleaseService.getMaxArrival())
                .isEqualTo(0);
    }
}
