package com.localmind.mcp_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.localmind.mcp_server", "com.localmind.memory", "com.localmind.controller", "com.localmind.service", "com.localmind.config"})
@EnableJpaRepositories(basePackages = {"com.localmind.memory"})
@EntityScan(basePackages = {"com.localmind.memory"})
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
}
