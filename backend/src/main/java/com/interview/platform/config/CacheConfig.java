package com.interview.platform.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String AVATAR_VIDEOS_CACHE = "avatarVideos";
    public static final String TTS_AUDIO_CACHE = "ttsAudio";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(AVATAR_VIDEOS_CACHE, TTS_AUDIO_CACHE);

        // Configure default cache settings:
        // - Expire entries 2 hours after write (sufficient for session locality)
        // - Maximum 1000 entries (prevents heap exhaustion)
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .maximumSize(1000)
                .recordStats());

        return cacheManager;
    }
}
