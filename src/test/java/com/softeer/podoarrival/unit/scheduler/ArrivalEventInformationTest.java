package com.softeer.podoarrival.unit.scheduler;

import com.softeer.podoarrival.event.model.entity.EventType;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceJavaImpl;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceRedisImpl;
import com.softeer.podoarrival.unit.base.ArrivalEventInformationBase;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ArrivalEventInformationTest extends ArrivalEventInformationBase {

    @Test
    @Transactional
    @DisplayName("이벤트 정보 세팅 스케줄러 동작 테스트 (성공 - Mysql에 데이터가 존재하는 경우)")
    public void setArrivalEventCountSuccess_Data_Exists() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        // given
        EventType arrivalType = EventType.builder()
                .id(1L)
                .type("arrival")
                .build();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CHECK = false;
                return null;
            }
        }).when(arrivalEventReleaseServiceImpl).setCheckFlag(false);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                START_DATE = true;
                return null;
            }
        }).when(arrivalEventReleaseServiceImpl).setStartDate(true);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                START_TIME = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0));
                return null;
            }
        }).when(arrivalEventReleaseServiceImpl).setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)));

        when(eventTypeRepository.findById(1L))
                .thenReturn(Optional.ofNullable(arrivalType));
        when(eventRepository.findFirstByEventTypeAndStartAtBetween(arrivalType, startOfDay, endOfDay))
                .thenReturn(eventSample);
        when(eventRewardRepository.findAllByEvent(eventSample))
                .thenReturn(List.of(eventReward1, eventReward2));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                REWORD_COUNT = 60;
                return null;
            }
        }).when(arrivalEventReleaseServiceImpl).setMaxArrival(60);

//        when(arrivalEventReleaseServiceImpl.setMaxArrival(any()))
//                .thenAnswer(
//                        new Answer() {
//                            @Override
//                            public Void answer(InvocationOnMock invocation) throws Throwable {
//                                REWORD_COUNT = (int) invocation.getArguments()[0];
//                            }
//                        }
//                );

        // when
        arrivalEventService.setArrivalEventInformation();

        // then
        Assertions.assertThat(REWORD_COUNT)
                .isEqualTo(60);
        Assertions.assertThat(START_TIME)
                .isEqualTo(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)));
    }

    @Test
    @Transactional
    @DisplayName("이벤트 정보 세팅 스케줄러 동작 테스트 (성공 - Mysql에 데이터가 존재하지 않는 경우)")
    public void setArrivalEventCountSuccess_Data_Not_Exists() {
        // given
        EventType arrivalType = EventType.builder()
                .id(1L)
                .type("arrival")
                .build();

        when(eventTypeRepository.findById(1L))
                .thenReturn(Optional.ofNullable(arrivalType));
        when(eventRepository.findFirstByEventTypeAndStartAtBetween(any(), any(), any()))
                .thenReturn(null);

        // when
        arrivalEventService.setArrivalEventInformation();

        // then
        Assertions.assertThat(arrivalEventReleaseServiceImpl.getMaxArrival())
                .isEqualTo(0);
        Assertions.assertThat(arrivalEventReleaseServiceImpl.getMaxArrival())
                .isEqualTo(0);
    }
}
