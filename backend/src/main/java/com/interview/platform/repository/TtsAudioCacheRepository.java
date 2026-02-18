package com.interview.platform.repository;

import com.interview.platform.model.TtsAudioCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TtsAudioCacheRepository extends JpaRepository<TtsAudioCache, Long> {

    Optional<TtsAudioCache> findByCacheKey(String cacheKey);
}
