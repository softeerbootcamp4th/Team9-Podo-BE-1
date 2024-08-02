package com.softeer.podoarrival.mapper;

import com.softeer.podoarrival.event.model.dto.GetQuizResponseDto;
import com.softeer.podoarrival.event.model.entity.Quiz;

public class QuizMapper {

    public static GetQuizResponseDto mapQuizToGetQuizResponseDto(Quiz quiz) {
        return new GetQuizResponseDto(
                quiz.getQuestion(),
                quiz.getChoice1(),
                quiz.getChoice2(),
                quiz.getChoice3(),
                quiz.getChoice4(),
                quiz.getAnswer()
        );
    }
}
