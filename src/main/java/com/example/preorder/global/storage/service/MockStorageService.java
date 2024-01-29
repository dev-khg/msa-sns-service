package com.example.preorder.global.storage.service;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockStorageService implements StorageService {
    private final ServletContext context;

    @Override
    public String uploadFile(MultipartFile file, Bucket bucket) {
        String savedName = UUID.randomUUID().toString();
        String absolutePath = context.getRealPath("/uploads");

        try {
            if(! new File(absolutePath).exists())
            {
                new File(absolutePath).mkdir();
            }

            File dest = new File(absolutePath);
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return savedName;
    }
}
