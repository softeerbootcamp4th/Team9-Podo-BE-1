package com.softeer.podoarrival.unit.event;

import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.model.entity.Role;
import com.softeer.podoarrival.event.repository.ArrivalUserRepository;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceJavaImpl;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceRedisImpl;
import com.softeer.podoarrival.event.service.ArrivalEventService;
import com.softeer.podoarrival.security.AuthInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ArrivalEventComplexServiceTest {

    @InjectMocks
    private ArrivalEventService arrivalEventService;

    @Mock
    private ArrivalEventReleaseServiceJavaImpl javaService;

    @Mock
    private ArrivalEventReleaseServiceRedisImpl redisService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private ArrivalUserRepository arrivalUserRepository;

    private AuthInfo authInfo;
    private ArrivalApplicationResponseDto expectedResponse;

    @BeforeEach
    public void setup() {
        authInfo = new AuthInfo("name", "phoneNum", Role.ROLE_USER);
        expectedResponse = new ArrivalApplicationResponseDto(true, "name", "phoneNum", 1);
    }

    @Test
    @DisplayName("다양한 구현체에 대해 선착순 이벤트 테스트 (성공 - Redis)")
    public void testApplyEventWithRedisImplementations_Success() {
        // given
        doReturn(CompletableFuture.completedFuture(expectedResponse)).when(redisService).applyEvent(authInfo);
        arrivalEventService = new ArrivalEventService(redisService);

        // when
        CompletableFuture<ArrivalApplicationResponseDto> result = arrivalEventService.applyEvent(authInfo);

        // then
        Assertions.assertThat(result).isCompletedWithValue(expectedResponse);
    }

    @Test
    @DisplayName("다양한 구현체에 대해 선착순 이벤트 테스트 (성공 - Java)")
    public void testApplyEventWithJavaImplementations_Success() {
        // given
        doReturn(CompletableFuture.completedFuture(expectedResponse)).when(javaService).applyEvent(authInfo);
        arrivalEventService = new ArrivalEventService(javaService);

        // when
        CompletableFuture<ArrivalApplicationResponseDto> result = arrivalEventService.applyEvent(authInfo);

        // then
        Assertions.assertThat(result).isCompletedWithValue(expectedResponse);
    }
}
