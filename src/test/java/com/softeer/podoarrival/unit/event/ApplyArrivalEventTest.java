package com.softeer.podoarrival.unit.event;

import com.softeer.podoarrival.unit.base.ArrivalEventBase;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseServiceRedisImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.redisson.api.BatchResult;
import org.redisson.api.RSetAsync;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplyArrivalEventTest extends ArrivalEventBase {

    @Test
    @DisplayName("선착순 이벤트 응모 (성공)")
    public void applyArrivalEventSuccess() throws ExecutionException, InterruptedException {
        // given
        when(redissonClient.createBatch()).thenReturn(batch);

        when(batch.getSet(anyString())).thenAnswer((Answer<RSetAsync>) invocation -> {
            RSetAsync result = mock(RSetAsync.class);
            when(result.addAsync(anyString())).thenReturn(null);
            when(result.sizeAsync()).thenReturn(null);
            return result;
        });

        when(batch.execute()).thenAnswer((Answer<BatchResult<Object>>) invocation -> {
            BatchResult result = mock(BatchResult.class);
            when(result.getResponses()).thenReturn(Arrays.asList(true, 1));
            return result;
        });

        // when
        CompletableFuture<ArrivalApplicationResponseDto> responseFuture = arrivalEventReleaseServiceRedisImpl.applyEvent(authInfo);
        ArrivalApplicationResponseDto response = responseFuture.get();  // 비동기 결과를 기다림

        // then
        assertThat(response.isSuccess()).isEqualTo(true);
        assertThat(response.getName()).isEqualTo("user");
        assertThat(response.getPhoneNum()).isEqualTo("01012345678");
        assertThat(response.getGrade()).isEqualTo(1);
    }

    @Test
    @DisplayName("선착순 이벤트 응모 (실패 - 선착순 순위 안에 들지 못했을 경우)")
    public void applyArrivalEventFail_Late() throws ExecutionException, InterruptedException {
        // given
        try {
            java.lang.reflect.Field checkField = ArrivalEventReleaseServiceRedisImpl.class.getDeclaredField("CHECK");
            checkField.setAccessible(true); // private 필드 접근 허용
            checkField.set(arrivalEventReleaseServiceRedisImpl, true); // 값 설정
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // when
        CompletableFuture<ArrivalApplicationResponseDto> responseFuture = arrivalEventReleaseServiceRedisImpl.applyEvent(authInfo);
        ArrivalApplicationResponseDto response = responseFuture.get();  // 비동기 결과를 기다림

        // then
        assertThat(response.isSuccess()).isEqualTo(false);
        assertThat(response.getName()).isEqualTo("user");
        assertThat(response.getPhoneNum()).isEqualTo("01012345678");
        assertThat(response.getGrade()).isEqualTo(-1);
    }

    @Test
    @DisplayName("선착순 이벤트 응모 (실패 - 중복 참여)")
    public void applyArrivalEventFail_Duplicate() {
        // given
        when(redissonClient.createBatch()).thenReturn(batch);

        when(batch.getSet(anyString())).thenAnswer((Answer<RSetAsync>) invocation -> {
            RSetAsync result = mock(RSetAsync.class);
            when(result.addAsync(anyString())).thenReturn(null);
            when(result.sizeAsync()).thenReturn(null);
            return result;
        });

        when(batch.execute()).thenAnswer((Answer<BatchResult<Object>>) invocation -> {
            BatchResult result = mock(BatchResult.class);
            when(result.getResponses()).thenReturn(Arrays.asList(false, 1));
            return result;
        });

        // when
        CompletableFuture<ArrivalApplicationResponseDto> responseFuture = arrivalEventReleaseServiceRedisImpl.applyEvent(authInfo);

        // then
        assertThatThrownBy(() -> responseFuture.get())
                .isInstanceOf(ExecutionException.class);
    }
}
