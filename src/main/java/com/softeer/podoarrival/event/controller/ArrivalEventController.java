package com.softeer.podoarrival.event.controller;


import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.event.model.dto.ArrivalApplicationResponseDto;
import com.softeer.podoarrival.event.service.ArrivalEventService;
import com.softeer.podoarrival.security.Auth;
import com.softeer.podoarrival.security.AuthInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/arrival")
@Slf4j
public class ArrivalEventController {

    private final ArrivalEventService arrivalEventService;

    @PostMapping("/application")
    @Operation(summary = "선착순 응모용 Api")
    public CommonResponse<ArrivalApplicationResponseDto> arrivalEventApplicaion(@Auth AuthInfo authInfo){
        return new CommonResponse<>(arrivalEventService.application(authInfo));
    }


}
