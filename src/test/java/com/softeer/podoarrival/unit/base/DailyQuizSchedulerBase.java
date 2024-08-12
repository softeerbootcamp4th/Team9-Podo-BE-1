package com.softeer.podoarrival.unit.base;

import com.softeer.podoarrival.event.repository.QuizRepository;
import com.softeer.podoarrival.event.repository.RedisRepository;
import com.softeer.podoarrival.event.scheduler.DailyQuizScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

@ExtendWith(MockitoExtension.class)
public class DailyQuizSchedulerBase {

    @Mock
    protected Clock clock;

    @Mock
    protected RedisRepository redisRepository;

    @Mock
    protected QuizRepository quizRepository;

    @InjectMocks
    protected DailyQuizScheduler quizScheduler;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }
}
