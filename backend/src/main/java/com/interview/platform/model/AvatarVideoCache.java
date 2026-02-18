package com.interview.platform.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Persistent cache entry mapping a deterministic cache key to an S3 object key
 * for a previously generated avatar video.
 *
 * <p>
 * The cache key is a SHA-256 hash of the question text + voice/avatar
 * configuration. This ensures that identical questions with the same
 * voice settings reuse existing videos instead of regenerating them.
 * </p>
 *
 * @see com.interview.platform.service.CachedAvatarService
 */
@Entity
@Table(name = "avatar_video_cache")
public class AvatarVideoCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", nullable = false, unique = true, length = 64)
    private String cacheKey;

    @Column(name = "s3_key", nullable = false, length = 512)
    private String s3Key;

    @Column(name = "created_at")
    private Instant createdAt;

    protected AvatarVideoCache() {
        // JPA
    }

    public AvatarVideoCache(String cacheKey, String s3Key) {
        this.cacheKey = cacheKey;
        this.s3Key = s3Key;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public String getS3Key() {
        return s3Key;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
