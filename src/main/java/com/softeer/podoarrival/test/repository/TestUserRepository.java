package com.softeer.podoarrival.test.repository;

import com.softeer.podoarrival.user.model.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepository extends JpaRepository<TestUser, Long> {

}
