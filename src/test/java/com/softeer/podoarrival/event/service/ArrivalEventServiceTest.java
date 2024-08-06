package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.PodoArrivalApplication;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import com.softeer.podoarrival.event.model.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ContextConfiguration(classes = PodoArrivalApplication.class)
class ArrivalEventServiceTest {

    @Autowired
    ArrivalEventService arrivalEventService;

    @Autowired
    RedissonClient redissonClient;

    @AfterEach
    void tearDown() {
        redissonClient.getKeys().flushdb();
        redissonClient.shutdown();
    }

    @Value("${MAX_COUNT}")
    private int MAX_COUNT;

    @Test
    @DisplayName("선착순 api 정확도 테스트")
    void applicationTest() throws InterruptedException {
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
                    CompletableFuture<ArrivalApplicationResponseDto> futureResponse = arrivalEventService.applyEvent(
                            new AuthInfo(
                                    "teat" + userId,
                                    "010-1234-5678-" + userId,
                                    Role.ROLE_USER
                            )
                    );
                    if(futureResponse.get().getResponse().equals("선착순 응모에 성공했습니다.")) count.getAndIncrement();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        redissonClient.getSet("arrivalset").clear();
        assertEquals(MAX_COUNT, count.get());
    }
}