package com.softeer.podoarrival.event.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "quizzes")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String answer;

    private LocalDate eventDate;

}
