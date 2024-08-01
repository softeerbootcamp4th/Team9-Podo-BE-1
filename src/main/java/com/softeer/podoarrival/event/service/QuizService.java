package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.GetQuizResponseDto;
import com.softeer.podoarrival.event.repository.QuizRepository;
import com.softeer.podoarrival.event.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Redis에 저장되어 있는 퀴즈 정보 가져오기
     * @return
     */
    @Transactional(readOnly = true)
    public GetQuizResponseDto getQuizInfo() {
        String question = redisRepository.getValues(QUESTION_KEY);
        String choice1 = redisRepository.getValues(CHOICE_KEYS[0]);
        String choice2 = redisRepository.getValues(CHOICE_KEYS[1]);
        String choice3 = redisRepository.getValues(CHOICE_KEYS[2]);
        String choice4 = redisRepository.getValues(CHOICE_KEYS[3]);
        String answer = redisRepository.getValues(ANSWER_KEYS);

        return new GetQuizResponseDto(question, choice1, choice2, choice3, choice4, answer);
    }
}
