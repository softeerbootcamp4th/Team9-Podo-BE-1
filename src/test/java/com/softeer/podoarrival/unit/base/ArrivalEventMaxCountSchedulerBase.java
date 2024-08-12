package com.softeer.podoarrival.unit.base;

import com.softeer.podoarrival.event.repository.EventRepository;
import com.softeer.podoarrival.event.repository.EventRewardRepository;
import com.softeer.podoarrival.event.repository.EventTypeRepository;
import com.softeer.podoarrival.event.scheduler.ArrivalEventMaxArrivalScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArrivalEventMaxCountSchedulerBase {

    @Mock
    protected EventRepository eventRepository;

    @Mock
    protected EventRewardRepository eventRewardRepository;

    @Mock
    protected EventTypeRepository eventTypeRepository;

    @InjectMocks
    protected ArrivalEventMaxArrivalScheduler arrivalEventMaxArrivalScheduler;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }
}
