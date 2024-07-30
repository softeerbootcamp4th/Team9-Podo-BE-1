package com.softeer.podoarrival.test.controller;

import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.security.Auth;
import com.softeer.podoarrival.security.AuthInfo;
import com.softeer.podoarrival.test.model.dto.TestSignUpRequestDto;
import com.softeer.podoarrival.test.model.dto.TestSignUpResponseDto;
import com.softeer.podoarrival.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final TestService testService;

    @GetMapping("/auth")
    @Operation(summary = "(테스트) Authorization 토큰 테스트용 Mock Api")
    public CommonResponse<String> testAuthentication(@Auth AuthInfo authInfo) {
        return new CommonResponse<>(authInfo.getPhoneNum());
    }

    @PostMapping("/signUp")
    @Operation(summary = "(테스트) Authorization 토큰 테스트용 회원가입 Mock Api")
    public CommonResponse<TestSignUpResponseDto> testSignUp(@Valid @RequestBody TestSignUpRequestDto dto) {
        return new CommonResponse<>(testService.testSignUp(dto));
    }
}
