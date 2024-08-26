package com.softeer.podoarrival.event.service;

import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.security.AuthInfo;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface ArrivalEventReleaseService {

    CompletableFuture<ArrivalApplicationResponseDto> applyEvent(AuthInfo authInfo);

    void setMaxArrival(int val);

    void setStartTime(LocalDateTime val);

    void setStartDate(Boolean val);

    int getMaxArrival();

    void setCheckFlag(boolean flag);

    LocalDateTime getStartTime();

    LocalDateTime getStartTimeStatic();

    boolean getStartDate();
}
