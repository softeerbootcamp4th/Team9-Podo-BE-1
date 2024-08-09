package com.softeer.podoarrival.unit.base;

import com.softeer.podoarrival.event.model.entity.Role;
import com.softeer.podoarrival.event.repository.ArrivalUserRepository;
import com.softeer.podoarrival.event.service.ArrivalEventReleaseService;
import com.softeer.podoarrival.security.AuthInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RSetAsync;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
public class ArrivalEventBase {

    @Mock
    protected RedissonClient redissonClient;

    @Mock
    protected ArrivalUserRepository arrivalUserRepository;

    @Mock
    protected RBatch batch;

    @Mock
    protected RSetAsync<String> setAsync;

    @Mock
    protected BatchResult<Object> batchResult;

    @InjectMocks
    protected ArrivalEventReleaseService arrivalEventReleaseService;

    protected AuthInfo authInfo;

    @BeforeEach
    public void setUp() {
        authInfo = new AuthInfo("user", "01012345678", Role.ROLE_USER);
        ArrivalEventReleaseService.setMaxArrival(5);
    }
}
