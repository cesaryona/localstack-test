package com.uplaod.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    public String createBucket(final String bucketName) {
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();

        CreateBucketResponse bucketResponse = s3Client.createBucket(bucketRequest);

        return "Bucket created successfully: " + bucketResponse.location();
    }

    public List<String> listFiles(final String bucketName) {
        var request = ListObjectsRequest.builder().bucket(bucketName).build();

        return s3Client.listObjects(request)
                .contents().stream()
                .map(S3Object::key)
                .toList();
    }

    public void uploadFile(final String bucketName, final String key, final String filePath) {
        var request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(request, Paths.get(filePath));
    }

    public byte[] downloadFile(final String bucketName, final String key) {
        var request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(request).asByteArray();
    }

    public void deleteFile(final String bucketName, final String key) {
        var request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}
