package hg.userservice.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {
    @Value("${upload.folder}")
    private String uploadFolder;

    @Override
    public String uploadAsync(MultipartFile file) {
        String savedFileName = UUID.randomUUID().toString();

        CompletableFuture.supplyAsync(() -> {
            try {
                Path uploadPath = Paths.get(uploadFolder);

                if (!uploadPath.toFile().exists()) {
                    uploadPath.toFile().mkdirs();
                }

                Path filePath = uploadPath.resolve(savedFileName);
                file.transferTo(filePath);
            } catch (Exception e) {
                log.info("Fail to upload file.");
            }
            return null;
        });

        return uploadFolder + "/" + savedFileName;
    }

    @Override
    public void deleteAsync(String path) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Path fullPath = Paths.get(path);
                Files.delete(fullPath);
            } catch (Exception e) {
                log.error("Fail to remove file [{}]", path);
            }
            return null;
        });
    }
}
