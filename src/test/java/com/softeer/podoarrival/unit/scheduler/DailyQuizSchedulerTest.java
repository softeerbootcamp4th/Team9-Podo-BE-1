package com.softeer.podoarrival.unit.scheduler;

import com.softeer.podoarrival.event.model.entity.Quiz;
import com.softeer.podoarrival.unit.base.DailyQuizSchedulerBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

public class DailyQuizSchedulerTest extends DailyQuizSchedulerBase {

    @Test
    @DisplayName("퀴즈 스케줄러 동작 테스트 (성공 - Mysql에 데이터가 존재하는 경우)")
    public void getQuizListSuccess_CacheHit() {
        // given
        when(quizRepository.findByEventDate(LocalDate.now()))
                .thenReturn(Quiz.builder()
                        .id(1L)
                        .question("질문입니다.")
                        .choice1("선택지 1")
                        .choice2("선택지 2")
                        .choice3("선택지 3")
                        .choice4("선택지 4")
                        .answer("A")
                        .build()
                );

        // when
        quizScheduler.setDailyQuiz();
    }

    @Test
    @DisplayName("퀴즈 스케줄러 동작 테스트 (성공 - Mysql에 데이터가 존재하지 않은 경우)")
    public void getQuizListFail_CacheMiss() {
        // given
        when(quizRepository.findByEventDate(LocalDate.now()))
                .thenReturn(null);

        // when
        quizScheduler.setDailyQuiz();
    }
}
