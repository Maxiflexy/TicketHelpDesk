package com.maxiflexy.tickethelpdeskapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        // Add your custom health check logic here
        boolean healthy = checkApplicationComponents();
        if (healthy) {
            return Health.up().withDetail("database", "Database is up and running").build();
        } else {
            return Health.down()
                    .withDetail("database", "Database is down")
                    .withDetail("error", "Could not connect to PostgreSQL")
                    .build();
        }
    }

    private boolean checkApplicationComponents() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
