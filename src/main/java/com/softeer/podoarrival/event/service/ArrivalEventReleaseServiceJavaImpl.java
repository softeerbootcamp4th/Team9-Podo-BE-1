package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.exception.ExistingUserException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.model.entity.ArrivalUser;
import com.softeer.podoarrival.event.model.entity.Role;
import com.softeer.podoarrival.event.repository.ArrivalUserRepository;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventReleaseServiceJavaImpl implements ArrivalEventReleaseService {

    private final ArrivalUserRepository arrivalUserRepository;

    private static int MAX_ARRIVAL = 100; // default
    private boolean CHECK = false;

    private static AtomicInteger count = new AtomicInteger(1);
    private static ConcurrentHashMap<String, Integer> hashMap = new ConcurrentHashMap<>();

    /**
     * 비동기와 Atomic 변수를 통해서 동시성을 처리
     * Hash function 통해서 전화번호 중복을 확인
     */
    @Async("arrivalExecutor")
    @Override
    public CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo) {
        return CompletableFuture.supplyAsync(() -> {

            if(CHECK){
                return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), -1);
            }

            //첫번째 응답
            if(hashMap.containsKey(authInfo.getPhoneNum())){
                throw new ExistingUserException("이미 응모한 전화번호입니다.");
            }

            int grade = count.getAndIncrement();
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
                log.info("전화번호 = {}", authInfo.getPhoneNum());
                return new ArrivalApplicationResponseDto(true, authInfo.getName(), authInfo.getPhoneNum(), grade);
            } else {
                CHECK = true;
                return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), grade);
            }
        });
    }

    public static void setMaxArrival(int val) {
        MAX_ARRIVAL = val;
    }

    public static int getMaxArrival() {
        return MAX_ARRIVAL;
    }
}
