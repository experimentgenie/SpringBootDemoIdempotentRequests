package com.example.springbootdemoidempotentrequests;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SpringBootDemoIdempotentRequestsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoIdempotentRequestsApplication.class, args);
    }

    @Bean
    public LoadingCache<String, Object> loadingCache() {
        CacheLoader<String, Object> loader = new CacheLoader<>() {
            @Override
            @NonNull
            public String load(@NonNull String key) {
                return key;
            }
        };

        return CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(loader);
    }

}
