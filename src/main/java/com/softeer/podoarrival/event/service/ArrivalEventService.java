package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.exception.ExistingUserException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ArrivalEventService {

    private final RedissonClient redissonClient;

    private final String FINISHED = "finished";
    private final String ARRIVAL_SET = "arrivalset";
    private final String CHECK = "check";

    private final int MAX_ARRIVAL = 100;

    @Transactional
    public ArrivalApplicationResponseDto applyEvent(AuthInfo authInfo){
        LocalDate now = LocalDate.now();
        RBucket<String> check = redissonClient.getBucket(now + CHECK);

        if(check.get() != null && check.get().equals(FINISHED)){
            return new ArrivalApplicationResponseDto("선착순 응모에 실패했습니다.");
        }

        RBatch batch = redissonClient.createBatch();
        batch.getSet(now + ARRIVAL_SET).addAsync(authInfo.getPhoneNum());
        batch.getSet(now + ARRIVAL_SET).sizeAsync();
        BatchResult<?> res = batch.execute();

        //첫번째 응답
        if(!(boolean) res.getResponses().get(0)){
            throw new ExistingUserException("이미 응모한 전화번호입니다.");
        }

        if((int) res.getResponses().get(1) <= MAX_ARRIVAL){
            return new ArrivalApplicationResponseDto("선착순 응모에 성공했습니다.");
        }else{
            check.set(FINISHED);
            return new ArrivalApplicationResponseDto("선착순 응모에 성공했습니다.");
        }
    }


}
