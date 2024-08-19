package com.softeer.podoarrival.unit.quiz;

import com.softeer.podoarrival.event.exception.DailyQuizNotExistsException;
import com.softeer.podoarrival.unit.base.QuizServiceBase;
import com.softeer.podoarrival.event.model.dto.GetQuizResponseDto;
import com.softeer.podoarrival.event.model.entity.Quiz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class GetQuizListTest extends QuizServiceBase{

    @Test
    @DisplayName("퀴즈 리스트 불러오기 (성공 - 레디스에 정보가 존재한 경우)")
    public void getQuizListSuccess_CacheHit() {
        // given
        when(redisRepository.getValues(QUESTION_KEY)).thenReturn("질문입니다");
        when(redisRepository.getValues(CHOICE_KEYS[0])).thenReturn("선택지 1");
        when(redisRepository.getValues(CHOICE_KEYS[1])).thenReturn("선택지 2");
        when(redisRepository.getValues(CHOICE_KEYS[2])).thenReturn("선택지 3");
        when(redisRepository.getValues(CHOICE_KEYS[3])).thenReturn("선택지 4");
        when(redisRepository.getValues(ANSWER_KEYS)).thenReturn("A");

        // when
        GetQuizResponseDto result = quizService.getQuizInfo();

        // then
        assertThat(result.getQuestion()).isEqualTo("질문입니다");
        assertThat(result.getChoice1()).isEqualTo("선택지 1");
        assertThat(result.getChoice2()).isEqualTo("선택지 2");
        assertThat(result.getChoice3()).isEqualTo("선택지 3");
        assertThat(result.getChoice4()).isEqualTo("선택지 4");
        assertThat(result.getAnswer()).isEqualTo("A");
    }

    @Test
    @DisplayName("퀴즈 리스트 불러오기 (성공 - 레디스에 정보가 존재하지 않은 경우)")
    public void getQuizListSuccess_CacheMiss() {
        // given
        when(redisRepository.getValues(QUESTION_KEY)).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[0])).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[1])).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[2])).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[3])).thenReturn(null);
        when(redisRepository.getValues(ANSWER_KEYS)).thenReturn(null);
        when(quizRepository.findByEventDate(LocalDate.now()))
                .thenReturn(Quiz.builder()
                        .question("질문입니다")
                        .choice1("선택지 1")
                        .choice2("선택지 2")
                        .choice3("선택지 3")
                        .choice4("선택지 4")
                        .answer("A")
                        .build()
                );

        // when
        GetQuizResponseDto result = quizService.getQuizInfo();

        // then
        assertThat(result.getQuestion()).isEqualTo("질문입니다");
        assertThat(result.getChoice1()).isEqualTo("선택지 1");
        assertThat(result.getChoice2()).isEqualTo("선택지 2");
        assertThat(result.getChoice3()).isEqualTo("선택지 3");
        assertThat(result.getChoice4()).isEqualTo("선택지 4");
        assertThat(result.getAnswer()).isEqualTo("A");
    }

    @Test
    @DisplayName("퀴즈 리스트 불러오기 (실패 - 데이터베이스에도 정보가 존재하지 않는 경우)")
    public void getQuizListFail_CacheMiss() {
        // given
        when(redisRepository.getValues(QUESTION_KEY)).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[0])).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[1])).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[2])).thenReturn(null);
        when(redisRepository.getValues(CHOICE_KEYS[3])).thenReturn(null);
        when(redisRepository.getValues(ANSWER_KEYS)).thenReturn(null);
        when(quizRepository.findByEventDate(LocalDate.now()))
                .thenReturn(null);

        // when-then
        assertThatThrownBy(() -> quizService.getQuizInfo())
                .isInstanceOf(DailyQuizNotExistsException.class);
    }

}
