package com.softeer.podoarrival.event.repository;

import com.softeer.podoarrival.event.model.entity.Event;
import com.softeer.podoarrival.event.model.entity.EventReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRewardRepository extends JpaRepository<EventReward, Long> {
	int countByEvent(Event event);
}
