package com.interview.platform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWS SDK v2 configuration for S3 operations.
 *
 * <h3>P1 Migration from SDK v1:</h3>
 * <ul>
 *   <li>Replaced {@code AmazonS3} (SDK v1) with {@code S3Client} (SDK v2).</li>
 *   <li>Added {@code S3Presigner} for generating presigned PUT and GET URLs.</li>
 *   <li>Supports both static credentials (dev/local) and IAM role-based
 *       authentication (production on EC2/ECS/EKS) via {@code DefaultCredentialsProvider}.</li>
 * </ul>
 *
 * <h3>Credential Resolution Strategy:</h3>
 * <p>When {@code aws.access.key.id} and {@code aws.secret.access.key} are both
 * set and non-empty, static credentials are used. This is the typical local/dev
 * setup. In production (EC2/ECS/EKS), leave these properties blank or unset and
 * the {@code DefaultCredentialsProvider} chain will pick up IAM role credentials
 * automatically (instance profile, ECS task role, IRSA, etc.).</p>
 *
 * <h3>Important:</h3>
 * <p>Both {@code S3Client} and {@code S3Presigner} must use the same region and
 * credentials provider, otherwise presigned URLs may not work for the target bucket.</p>
 */
@Configuration
public class S3Config {

    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    @Value("${aws.access.key.id:}")
    private String accessKeyId;

    @Value("${aws.secret.access.key:}")
    private String secretAccessKey;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    /**
     * Build the appropriate credentials provider based on configuration.
     *
     * <p>If static credentials are provided (non-empty), uses
     * {@link StaticCredentialsProvider}. Otherwise, falls back to
     * {@link DefaultCredentialsProvider} which checks, in order:
     * environment variables, system properties, web identity token,
     * AWS config/credential files, ECS container credentials, and
     * EC2 instance profile credentials.</p>
     */
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (accessKeyId != null && !accessKeyId.isBlank()
                && secretAccessKey != null && !secretAccessKey.isBlank()) {
            log.info("Using static AWS credentials (dev/local mode)");
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey));
        }

        log.info("Using DefaultCredentialsProvider (IAM role / environment chain)");
        return DefaultCredentialsProvider.create();
    }

    /**
     * S3Client for standard object operations (PUT, GET, HEAD, DELETE).
     *
     * <p>Thread-safe and designed to be shared across the application.
     * The client manages its own connection pool internally.</p>
     */
    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    /**
     * S3Presigner for generating presigned PUT and GET URLs.
     *
     * <p>Used by {@code S3StorageService} to create:
     * <ul>
     *   <li>Presigned PUT URLs — frontend uploads video responses directly to S3</li>
     *   <li>Presigned GET URLs — on-demand URL generation for playback/download</li>
     * </ul>
     *
     * <p>Must use the same region and credentials as the S3Client.</p>
     */
    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    /**
     * Expose the bucket name as a bean for injection into storage services.
     */
    @Bean("s3BucketName")
    public String s3BucketName() {
        return bucketName;
    }
}
