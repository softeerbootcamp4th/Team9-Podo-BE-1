package com.softeer.podoarrival.event.repository;


import com.softeer.podoarrival.event.model.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
}
