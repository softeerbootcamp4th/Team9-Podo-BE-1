package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.GetQuizResponseDto;
import com.softeer.podoarrival.event.model.entity.Quiz;
import com.softeer.podoarrival.event.repository.QuizRepository;
import com.softeer.podoarrival.event.repository.RedisRepository;
import com.softeer.podoarrival.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final RedisRepository redisRepository;

    private final String QUESTION_KEY = "quiz::question";
    private final String[] CHOICE_KEYS = {"quiz::choice1", "quiz::choice2", "quiz::choice3", "quiz::choice4"};
    private final String ANSWER_KEYS = "quiz::answer";

    /**
     * Redis에 저장되어 있는 퀴즈 정보 가져오기 (스케줄러에 의해서 자동으로 redis로 저장된다)
     * 만약 저장되어 있지 않다면, 직접 가져온 후 저장
     */
    @Transactional
    public GetQuizResponseDto getQuizInfo() {

        GetQuizResponseDto quizDto = new GetQuizResponseDto(
                redisRepository.getValues(QUESTION_KEY),
                redisRepository.getValues(CHOICE_KEYS[0]),
                redisRepository.getValues(CHOICE_KEYS[1]),
                redisRepository.getValues(CHOICE_KEYS[2]),
                redisRepository.getValues(CHOICE_KEYS[3]),
                redisRepository.getValues(ANSWER_KEYS)
        );

        // cache miss
        if(quizDto.isInvalid()) {
            Quiz quiz = quizRepository.findByEventDate(LocalDate.now());
            setQuizToRedis(quiz);
            return QuizMapper.mapQuizToGetQuizResponseDto(quiz);

        }
        // cache hit
        else {
            return quizDto;
        }
    }

    private void setQuizToRedis(Quiz quiz) {
        redisRepository.setValues(QUESTION_KEY, quiz.getQuestion());
        redisRepository.setValues(CHOICE_KEYS[0], quiz.getChoice1());
        redisRepository.setValues(CHOICE_KEYS[1], quiz.getChoice2());
        redisRepository.setValues(CHOICE_KEYS[2], quiz.getChoice3());
        redisRepository.setValues(CHOICE_KEYS[3], quiz.getChoice4());
        redisRepository.setValues(ANSWER_KEYS, quiz.getAnswer());
    }
}
