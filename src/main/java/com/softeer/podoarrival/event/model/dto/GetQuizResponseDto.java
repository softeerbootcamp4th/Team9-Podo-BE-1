package com.softeer.podoarrival.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetQuizResponseDto {
    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String answer;
}
