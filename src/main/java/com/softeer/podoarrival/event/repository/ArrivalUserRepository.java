package com.softeer.podoarrival.event.repository;

import com.softeer.podoarrival.event.model.entity.ArrivalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
}
