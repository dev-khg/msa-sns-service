package com.example.preorder.global.storage.service;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    @Getter
    enum Bucket {
        IMAGE("/image-bucket");

        private final String bucketName;

        Bucket(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    String uploadFile(MultipartFile file, Bucket bucket);
}
