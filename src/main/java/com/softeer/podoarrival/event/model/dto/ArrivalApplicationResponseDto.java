package com.softeer.podoarrival.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrivalApplicationResponseDto {
    private boolean success;
    private String name;
    private String phoneNum;
    private int grade;
}
