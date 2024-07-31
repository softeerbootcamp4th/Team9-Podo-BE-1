package com.softeer.podoarrival.event.service;


import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.event.exception.ExistingUserException;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ArrivalEventService {
    private final RedissonClient redissonClient;
    @Value("${secret.max-arrival-count}")
    private int maxArrival;
    private boolean maxarrived = false;
    private final String finished = "finished";

    @Transactional
    public ArrivalApplicationResponseDto application(AuthInfo authInfo){
        LocalDate now = LocalDate.now();
        RBucket<String> check = redissonClient.getBucket(now.toString() + "check");

        if(check.get() != null && check.get().equals(finished)){
            return new ArrivalApplicationResponseDto("failed");
        }

        RBatch batch = redissonClient.createBatch();
        batch.getSet(now.toString() + "arrivalset").addAsync(authInfo.getPhoneNum());
        batch.getSet(now.toString() + "arrivalset").sizeAsync();
        BatchResult<?> res = batch.execute();

        //첫번째 응답
        if(!(boolean) res.getResponses().get(0)){
            throw new ExistingUserException("이미 응모한 전화번호입니다.");
        }

        if((int) res.getResponses().get(1) <= maxArrival){
            return new ArrivalApplicationResponseDto("success");
        }else{
            check.set(finished);
            return new ArrivalApplicationResponseDto("failed");
        }
    }


}
