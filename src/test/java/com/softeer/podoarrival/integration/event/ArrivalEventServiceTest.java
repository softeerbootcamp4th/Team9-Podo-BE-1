package com.softeer.podoarrival.integration.event;

import com.softeer.podoarrival.event.exception.EventClosedException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.model.entity.Role;
import com.softeer.podoarrival.event.repository.ArrivalUserRepository;
import com.softeer.podoarrival.event.repository.EventRepository;
import com.softeer.podoarrival.event.repository.EventRewardRepository;
import com.softeer.podoarrival.event.repository.EventTypeRepository;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceJavaImpl;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceRedisImpl;
import com.softeer.podoarrival.event.service.ArrivalEventService;
import com.softeer.podoarrival.security.AuthInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ContextConfiguration(classes = {ArrivalEventServiceTest.TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ArrivalEventServiceTest {

    @Autowired
    @Qualifier("redisEventService")
    private ArrivalEventService redisEventService;

    @Autowired
    @Qualifier("javaEventService")
    private ArrivalEventService javaEventService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ArrivalUserRepository arrivalUserRepository;

    @MockBean
    private LocalTime localTime;

    @AfterEach
    void tearDown() {
        redissonClient.getKeys().deleteByPattern("*arrivalset");
        redissonClient.shutdown();
        arrivalUserRepository.deleteAllInBatch();
    }

    private int MAX_COUNT = 100;

    @Test
    @DisplayName("선착순 api 정확도 테스트 - Redis")
    void redisApplyTest() throws InterruptedException {
        //given
        int threadCount = 100000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        AtomicInteger count = new AtomicInteger();

        //when
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    CompletableFuture<ArrivalApplicationResponseDto> futureResponse = redisEventService.applyEvent(
                            new AuthInfo(
                                    "test" + userId,
                                    "010-1234-5678-" + userId,
                                    Role.ROLE_USER
                            )
                    );
                    if(futureResponse.get().isSuccess()) count.getAndIncrement();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        assertEquals(MAX_COUNT, count.get());
    }

    @Test
    @DisplayName("선착순 api 정확도 테스트 - Java")
    void javaApplyTest() throws InterruptedException {
        //given
        int threadCount = 100000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        AtomicInteger count = new AtomicInteger();

        //when
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    CompletableFuture<ArrivalApplicationResponseDto> futureResponse = javaEventService.applyEvent(
                            new AuthInfo(
                                    "test" + userId,
                                    "010-1234-5678-" + userId,
                                    Role.ROLE_USER
                            )
                    );
                    if(futureResponse.get().isSuccess()) count.getAndIncrement();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        assertEquals(MAX_COUNT, count.get());
    }

    @Test
    @DisplayName("선착순 api 시간 외 오류 테스트")
    void eventOutOfTimeTest() throws NoSuchFieldException, IllegalAccessException {
        //given
        Field startDate = ArrivalEventReleaseServiceRedisImpl.class.getDeclaredField("START_DATE");
        Field startTime = ArrivalEventReleaseServiceRedisImpl.class.getDeclaredField("START_TIME");
        startDate.setAccessible(true); // private 필드를 접근 가능하도록 설정
        startTime.setAccessible(true);
        startDate.set(redisEventService, true);  // private 필드 값을 변경
        startTime.set(redisEventService, LocalTime.now().plusHours(1));

        //when
        CompletableFuture<ArrivalApplicationResponseDto> futureResponse = redisEventService.applyEvent(
                new AuthInfo(
                        "test",
                        "010-1234-5678-",
                        Role.ROLE_USER
                )
        );

        //then
        assertThrows(ExecutionException.class, futureResponse::get);
    }

    @Configuration
    static class TestConfig {

        @Bean
        @Qualifier("redisEventService")
        public ArrivalEventService arrivalEventRedisService(ArrivalEventReleaseServiceRedisImpl arrivalEventReleaseServiceRedisImpl) {
            return new ArrivalEventService(
                    arrivalEventReleaseServiceRedisImpl,
                    eventTypeRepository,
                    eventRewardRepository,
                    eventRepository
            );
        }

        @Bean
        @Qualifier("javaEventService")
        public ArrivalEventService arrivalEventJavaService(ArrivalEventReleaseServiceJavaImpl arrivalEventReleaseServiceJavaImpl) {
            return new ArrivalEventService(
                    arrivalEventReleaseServiceJavaImpl,
                    eventTypeRepository,
                    eventRewardRepository,
                    eventRepository
            );
        }

        @Bean
        public ArrivalEventReleaseServiceRedisImpl arrivalEventReleaseServiceRedisImpl() {
            return new ArrivalEventReleaseServiceRedisImpl(redisson(), arrivalUserRepository());
        }

        @Bean
        public ArrivalEventReleaseServiceJavaImpl arrivalEventReleaseServiceJavaImpl() {
            return new ArrivalEventReleaseServiceJavaImpl(arrivalUserRepository());
        }

        @MockBean
        public EventTypeRepository eventTypeRepository;

        @MockBean
        public EventRewardRepository eventRewardRepository;

        @MockBean
        public EventRepository eventRepository;

        @Bean
        public RedissonClient redisson() {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6379");

            // 인코딩 코덱 설정
            Codec codec = new StringCodec();
            config.setCodec(codec);

            return Redisson.create(config);
        }

        @Bean
        public ArrivalUserRepository arrivalUserRepository() {
            return Mockito.mock(ArrivalUserRepository.class);
        }
    }
}