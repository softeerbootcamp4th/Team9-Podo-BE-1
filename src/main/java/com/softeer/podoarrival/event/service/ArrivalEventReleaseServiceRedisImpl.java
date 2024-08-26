package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.exception.EventClosedException;
import com.softeer.podoarrival.event.exception.ExistingUserException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.model.entity.ArrivalUser;
import com.softeer.podoarrival.event.model.entity.Role;
import com.softeer.podoarrival.event.repository.ArrivalUserRepository;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventReleaseServiceRedisImpl implements ArrivalEventReleaseService {

    private static final Logger specialLogger = LoggerFactory.getLogger("arrivalEventLogger");

    private final RedissonClient redissonClient;
    private final ArrivalUserRepository arrivalUserRepository;

    private final String ARRIVAL_SET = "arrivalset";
    private boolean CHECK = false;
    private int MAX_ARRIVAL = 100; // default
    private LocalDateTime START_TIME = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0));
    private boolean START_DATE = true;

    /**
     * 비동기로 Redis 호출하는 메서드
     * Redisson set을 통해서 전화번호 중복 처리
     * Redisson lock 대신 set을 사용하여 인원수 처리
     */
    @Async("arrivalExecutor")
    @Override
    public CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo) {
        return CompletableFuture.supplyAsync(() -> {
            String redisKey = LocalDate.now() + ARRIVAL_SET;

            if(!START_DATE) throw new EventClosedException("이벤트 요일이 아닙니다.");
            if(LocalDateTime.now().isBefore(START_TIME)) throw new EventClosedException("이벤트 시간이 아닙니다.");

            if(CHECK){
                return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), -1);
            }

            RBatch batch = redissonClient.createBatch();
            batch.getSet(redisKey).addAsync(authInfo.getPhoneNum());
            batch.getSet(redisKey).sizeAsync();
            BatchResult<?> res = batch.execute();

            //첫번째 응답
            if(!(boolean) res.getResponses().get(0)){
                throw new ExistingUserException("이미 응모한 전화번호입니다.");
            }

            int grade = (int) res.getResponses().get(1);
            // 선착순 순위에 들었다면
            if(grade <= MAX_ARRIVAL){
                arrivalUserRepository.save(
                        ArrivalUser.builder()
                                .name(authInfo.getName())
                                .phoneNum(authInfo.getPhoneNum())
                                .role(Role.ROLE_USER)
                                .arrivalRank(grade)
                                .build()
                );
                log.info("[당첨] 유저 전화번호 = {}, 등수 = {}", authInfo.getPhoneNum(), grade);
                return new ArrivalApplicationResponseDto(true, authInfo.getName(), authInfo.getPhoneNum(), grade);
            } else {
                CHECK = true;
                START_TIME = LocalDateTime.of(LocalDate.now().plusDays(1), START_TIME.toLocalTime());
                return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), grade);
            }
        });
    }

    public void setMaxArrival(int val) {
        MAX_ARRIVAL = val;
    }

    public void setStartTime(LocalDateTime val) {
        START_TIME = val;
    }

    public void setStartDate(Boolean val) {
        START_DATE = val;
    }

    public void setCheckFlag(boolean flag) {
        CHECK = flag;
    }

    public int getMaxArrival() {
        return MAX_ARRIVAL;
    }

    public LocalDateTime getStartTime() {
        return START_TIME;
    }

    public LocalDateTime getStartTimeStatic(){
        return START_TIME;
    }

    public boolean getStartDate() {
        return START_DATE;
    }
}
