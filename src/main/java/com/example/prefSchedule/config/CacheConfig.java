package com.example.prefSchedule.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@EnableCaching //putem folosi acum @Cacheable, @CachePut, @CacheEvict
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
                "courses",
                "coursesById",
                "coursesByCode",
                "instructors",
                "instructorsById",
                "students",
                "studentsById",
                "packs",
                "packsById"
        ));

        return cacheManager;
    }
}
