package com.softeer.podoarrival.event.repository;

import com.softeer.podoarrival.event.model.entity.ArrivalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
    Optional<ArrivalUser> findByPhoneNum(String phoneNum);
}
