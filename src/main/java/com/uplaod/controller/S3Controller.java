package com.uplaod.controller;

import com.uplaod.service.S3Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/{bucketName}")
    public String create(@PathVariable("bucketName") String bucketName) {
        return s3Service.createBucket(bucketName);
    }

    @GetMapping("/{bucketName}")
    public List<String> findAll(@PathVariable("bucketName") String bucketName) {
        return s3Service.listFiles(bucketName);
    }

    @PostMapping("/upload/processar/{bucketName}")
    public String uploadFileProcessar(@PathVariable("bucketName") String bucketName, @RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        try {
            s3Service.uploadFile(bucketName, "processar/".concat(file.getOriginalFilename()), tempFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }

        return "File uploaded successfully";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("bucketName") String bucketName, @RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        try {
            s3Service.uploadFile(bucketName, file.getOriginalFilename(), tempFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }

        return "File uploaded successfully";
    }

    @GetMapping("/download/{bucketName}/{key}")
    public void downloadFile(@PathVariable String bucketName, @PathVariable String key, HttpServletResponse response) throws IOException {
        byte[] fileBytes;

        try {
            fileBytes = s3Service.downloadFile(bucketName, key);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from S3", e);
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + key + "\"");
        response.getOutputStream().write(fileBytes);
    }

    @DeleteMapping("/delete/{bucketName}/{key}")
    public String deleteFile(@PathVariable("bucketName") String bucketName, @PathVariable("key") String key) {
        s3Service.deleteFile(bucketName, key);
        return "File deleted successfully";
    }
}