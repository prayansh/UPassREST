package com.prayansh.upass;

import com.prayansh.upass.services.RenewService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by Prayansh on 2017-10-21.
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public RenewService renewServiceTemplate() {
        return new RenewService();
    }
}
