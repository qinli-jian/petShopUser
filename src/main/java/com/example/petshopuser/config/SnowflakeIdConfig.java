package com.example.petshopuser.config;

import com.example.petshopuser.utils.SnowflakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeIdConfig {
    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        // 实例化并返回你的UuidGenerator
        return new SnowflakeIdWorker(1,1);
    }
}
