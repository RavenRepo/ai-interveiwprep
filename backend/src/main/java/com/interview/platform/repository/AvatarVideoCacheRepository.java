package com.interview.platform.repository;

import com.interview.platform.model.AvatarVideoCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarVideoCacheRepository extends JpaRepository<AvatarVideoCache, Long> {

    Optional<AvatarVideoCache> findByCacheKey(String cacheKey);
}
