package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.exception.ExistingUserException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import com.softeer.podoarrival.user.model.entity.ArrivalUser;
import com.softeer.podoarrival.user.model.entity.Role;
import com.softeer.podoarrival.user.repository.ArrivalUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final RedissonClient redissonClient;
    private final ArrivalUserRepository arrivalUserRepository;

    private final String ARRIVAL_SET = "arrivalset";
    private boolean CHECK = false;

    private final int MAX_ARRIVAL = 100;

    @Transactional
    public ArrivalApplicationResponseDto applyEvent(AuthInfo authInfo){
        LocalDate now = LocalDate.now();

        if(CHECK){
            return new ArrivalApplicationResponseDto("선착순 응모에 실패했습니다.", -1);
        }
        log.info("전화번호 = {}", authInfo.getPhoneNum());

        RBatch batch = redissonClient.createBatch();
        batch.getSet(now + ARRIVAL_SET).addAsync(authInfo.getPhoneNum());
        batch.getSet(now + ARRIVAL_SET).sizeAsync();
        BatchResult<?> res = batch.execute();

        //첫번째 응답
        if(!(boolean) res.getResponses().get(0)){
            log.info("전화번호-중복 = {}", authInfo.getPhoneNum());
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
            return new ArrivalApplicationResponseDto("선착순 응모에 성공했습니다.", grade);
        } else {
            CHECK = true;
            return new ArrivalApplicationResponseDto("선착순 응모에 실패했습니다.", -1);
        }
    }


}
