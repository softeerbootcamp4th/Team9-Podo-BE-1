package com.softeer.podoarrival.user.repository;

import com.softeer.podoarrival.user.model.entity.ArrivalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalUserRepository extends JpaRepository<ArrivalUser, Long> {
    ArrivalUser findByNameAndPhoneNum(String name, String phoneNum);
    boolean existsByPhoneNum(String phoneNum);
}
