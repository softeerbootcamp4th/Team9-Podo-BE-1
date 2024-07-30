package com.softeer.podoarrival;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PodoArrivalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PodoArrivalApplication.class, args);
    }

}
