package com.softeer.podoarrival.event.scheduler;

import com.softeer.podoarrival.event.model.entity.Quiz;
import com.softeer.podoarrival.event.repository.QuizRepository;
import com.softeer.podoarrival.event.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 오늘의 퀴즈를 설정하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyQuizScheduler {

    private final RedisRepository redisRepository;
    private final QuizRepository quizRepository;

    private final String QUESTION_KEY = "quiz::question";
    private final String[] CHOICE_KEYS = {"quiz::choice1", "quiz::choice2", "quiz::choice3", "quiz::choice4"};
    private final String ANSWER_KEYS = "quiz::answer";

    /**
     * 특정 시간에 Mysql에서 Daily Quiz를 읽어와서 Redis에 저장
     */
    @Scheduled(cron = "0 30 3 * * *")
    public void setDailyQuiz() {
        LocalDate today = LocalDate.now();
        Quiz quiz = quizRepository.findByEventDate(today);

        if(quiz==null) {
            log.warn("Daily Quiz Processing Failed - Date: {}", today);
            return;
        }

        setQuizToRedis(quiz);
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
