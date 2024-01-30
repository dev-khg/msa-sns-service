package hg.userservice.infrastructure.storage;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface UploadService {

    String uploadAsync(MultipartFile file);

    void deleteAsync(String path);
}
