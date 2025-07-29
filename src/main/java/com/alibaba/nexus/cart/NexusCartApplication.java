package com.alibaba.nexus.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NexusCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusCartApplication.class, args);
    }

}
