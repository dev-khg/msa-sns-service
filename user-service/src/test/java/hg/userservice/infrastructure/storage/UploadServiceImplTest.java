package hg.userservice.infrastructure.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UploadServiceImplTest {
    @Value("${upload.folder}")
    private String uploadFolder;

    @Autowired
    UploadService uploadService;

    @Test
    @DisplayName("파일 업로드 및 삭제는 정상적으로 진행되어야 한다.")
    void valid_upload_and_delete_file() throws InterruptedException {
        // given
        MockMultipartFile file = createMockMultipartFile();

        // when

        // then
        String path = uploadService.uploadAsync(file);
        Thread.sleep(1000L); // Wait for uploading.
        assertTrue(Files.exists(Paths.get(path)));

        uploadService.deleteAsync(path);
        Thread.sleep(1000L); // Wait for deleting
        assertTrue(!Files.exists(Paths.get(path)));
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile("file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
    }
}