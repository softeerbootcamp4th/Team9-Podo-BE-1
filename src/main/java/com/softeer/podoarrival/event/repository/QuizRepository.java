package com.softeer.podoarrival.event.repository;

import com.softeer.podoarrival.event.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findByEventDate(LocalDate localDate);
}
