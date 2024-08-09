package com.softeer.podoarrival.unit.base;

import com.softeer.podoarrival.event.repository.QuizRepository;
import com.softeer.podoarrival.event.repository.RedisRepository;
import com.softeer.podoarrival.event.service.QuizService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class QuizServiceBase {

    protected String QUESTION_KEY;
    protected String[] CHOICE_KEYS;
    protected String ANSWER_KEYS;

    @Mock
    protected QuizRepository quizRepository;

    @Mock
    protected RedisRepository redisRepository;

    @InjectMocks
    protected QuizService quizService;

    protected MockedStatic<LocalDate> localDateMock;

    @BeforeEach
    public void setUp() {
        QUESTION_KEY = "quiz::question";
        CHOICE_KEYS = new String[]{"quiz::choice1", "quiz::choice2", "quiz::choice3", "quiz::choice4"};
        ANSWER_KEYS = "quiz::answer";

//        localDateMock = mockStatic(LocalDate.class);
//        localDateMock.when(LocalDate::now).thenReturn(LocalDate.of(2023, 12, 2));
    }

    @AfterEach
    public void tearDown() {
        // Close the mocked static after each test to avoid side effects
        if (localDateMock != null) {
            localDateMock.close();
        }
    }
}
