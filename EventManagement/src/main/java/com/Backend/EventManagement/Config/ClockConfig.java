package com.Backend.EventManagement.Config;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value ( "${app.timezone:Asia/Delhi}") String timezone) {
        return Clock.system(ZoneId.of(timezone));

    }

}
