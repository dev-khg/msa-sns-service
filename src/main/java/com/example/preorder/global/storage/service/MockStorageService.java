package com.example.preorder.global.storage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MockStorageService implements StorageService {
    @Override
    public String uploadFile(MultipartFile file, Bucket bucket) {
        return UUID.randomUUID().toString();
    }
}
