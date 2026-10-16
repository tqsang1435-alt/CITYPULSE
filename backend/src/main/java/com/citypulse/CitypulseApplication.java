package com.citypulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CitypulseApplication {

    static {
        // Prevent PostgreSQL "FATAL: invalid value for parameter TimeZone: Asia/Saigon"
        // on Windows machines in Vietnam where JVM defaults to deprecated Asia/Saigon.
        if ("Asia/Saigon".equals(TimeZone.getDefault().getID())) {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(CitypulseApplication.class, args);
    }
}
