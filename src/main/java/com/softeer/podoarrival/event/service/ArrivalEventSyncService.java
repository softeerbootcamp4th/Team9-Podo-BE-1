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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventSyncService {

    private static final Logger specialLogger = LoggerFactory.getLogger("arrivalEventLogger");

    private final RedissonClient redissonClient;
    private final ArrivalUserRepository arrivalUserRepository;
    private boolean CHECK = false;

    private final String ARRIVAL_SET = "arrivalset";
    private static int MAX_ARRIVAL = 40000; // default
    private static int count = 1;

    private static AtomicInteger countAtomic = new AtomicInteger(1);
    private static ConcurrentHashMap<String, Integer> hashMap = new ConcurrentHashMap<>();


    public ArrivalApplicationResponseDto applyEventWithRedis(AuthInfo authInfo) {
        String redisKey = LocalDate.now() + ARRIVAL_SET;

        if(CHECK){
            return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), -1);
        }

        RBatch batch = redissonClient.createBatch();
        batch.getSet(redisKey).addAsync(authInfo.getPhoneNum());
        batch.getSet(redisKey).sizeAsync();
        BatchResult<?> res = batch.execute();

        //첫번째 응답
        if(!(boolean) res.getResponses().get(0)){
            log.info("이미 응모한 전화번호 = {}", authInfo.getPhoneNum());
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
//            try {
//                Thread.sleep(15);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            return new ArrivalApplicationResponseDto(true, authInfo.getName(), authInfo.getPhoneNum(), grade);
        } else {
            CHECK = true;
            return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), grade);
        }
    }

    @Transactional
    public synchronized ArrivalApplicationResponseDto applyEventWithSynchronized(AuthInfo authInfo) {

        if(CHECK){
            return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), -1);
        }

        Optional<ArrivalUser> findUser = arrivalUserRepository.findByPhoneNum(authInfo.getPhoneNum());

        int grade = count++;
        if(grade<=MAX_ARRIVAL) {
            if(findUser.isEmpty()) {
                arrivalUserRepository.save(
                        ArrivalUser.builder()
                                .name(authInfo.getName())
                                .phoneNum(authInfo.getPhoneNum())
                                .role(Role.ROLE_USER)
                                .arrivalRank(grade)
                                .build()
                );
                return new ArrivalApplicationResponseDto(true, authInfo.getName(), authInfo.getPhoneNum(), grade);
            } else {
                throw new ExistingUserException("이미 응모한 전화번호입니다.");
            }
        } else {
            CHECK = true;
            return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), grade);
        }
    }

    @Transactional
    public ArrivalApplicationResponseDto applyEventWithJava(AuthInfo authInfo) {
        if(CHECK){
            return new ArrivalApplicationResponseDto(false, authInfo.getName(), authInfo.getPhoneNum(), -1);
        }

        //첫번째 응답
        if(hashMap.containsKey(authInfo.getPhoneNum())){
            throw new ExistingUserException("이미 응모한 전화번호입니다.");
        }

        int grade = countAtomic.getAndIncrement();
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
    }

}
