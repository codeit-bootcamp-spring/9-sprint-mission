package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class AWSS3Test {

    private static String accessKeyId;
    private static String secretKey;
    private static String region;
    private static String bucket;

    private static S3Client s3Client;
    private static S3Presigner s3Presigner;

    private static final String TEST_KEY = "test/hello.txt";
    private static final byte[] TEST_CONTENT = "Hello, S3!".getBytes(StandardCharsets.UTF_8);

    @BeforeAll
    static void setUp() throws IOException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(".env")) {
            props.load(is);
        }

        accessKeyId = props.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        region = props.getProperty("AWS_S3_REGION");
        bucket = props.getProperty("AWS_S3_BUCKET");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Test
    void upload() {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(TEST_KEY)
                .contentType("text/plain")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(TEST_CONTENT));

        System.out.println("업로드 완료: s3://" + bucket + "/" + TEST_KEY);
    }

    @Test
    void download() throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(TEST_KEY)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        byte[] downloaded = response.readAllBytes();

        assertThat(downloaded).isEqualTo(TEST_CONTENT);
        System.out.println("다운로드 완료: " + new String(downloaded, StandardCharsets.UTF_8));
    }

    @Test
    void generatePresignedUrl() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(TEST_KEY)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        URL presignedUrl = presignedRequest.url();

        assertThat(presignedUrl).isNotNull();
        assertThat(presignedUrl.toString()).contains(bucket);
        System.out.println("Presigned URL: " + presignedUrl);
    }
}
