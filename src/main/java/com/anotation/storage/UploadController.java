package com.anotation.storage;

import com.anotation.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final Path uploadRoot;

    public UploadController(@Value("${app.upload.dir:${user.home}/.data-labeling/uploads}") String uploadDir) {
        String dir = uploadDir != null && !uploadDir.isBlank() ? uploadDir : System.getProperty("user.home") + "/.data-labeling/uploads";
        this.uploadRoot = Paths.get(dir).toAbsolutePath().normalize();
    }

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> serve(
            @PathVariable String folder,
            @PathVariable String filename) {

        // Basic traversal protection
        if (folder.contains("..") || folder.contains("/") || folder.contains("\\")
                || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new NotFoundException("File not found.");
        }

        Path file = uploadRoot.resolve(folder).resolve(filename).normalize();
        if (!file.startsWith(uploadRoot) || !Files.exists(file) || !Files.isRegularFile(file)) {
            throw new NotFoundException("File not found.");
        }

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            String detected = Files.probeContentType(file);
            if (detected != null && !detected.isBlank()) {
                mediaType = MediaType.parseMediaType(detected);
            }
        } catch (IOException ignored) {
            // keep octet-stream
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}

