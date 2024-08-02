package com.softeer.podoarrival.event.controller;

import com.softeer.podoarrival.common.response.CommonResponse;
import com.softeer.podoarrival.event.model.dto.GetQuizResponseDto;
import com.softeer.podoarrival.event.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/quiz")
@RestController
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "퀴즈 리스트 받아오기")
    public CommonResponse<GetQuizResponseDto> getQuiz() {
        return new CommonResponse<>(quizService.getQuizInfo());
    }
}
