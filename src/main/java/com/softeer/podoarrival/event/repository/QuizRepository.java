package com.softeer.podoarrival.event.repository;

import com.softeer.podoarrival.event.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
