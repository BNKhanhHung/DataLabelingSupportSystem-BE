package com.anotation.storage;

import com.anotation.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final String SUPABASE_PLACEHOLDER = "placeholder.supabase.co";

    private final String supabaseUrl;
    private final String serviceKey;
    private final String bucket;
    private final String uploadDir;
    private final String baseUrl;
    private final HttpClient httpClient;

    public SupabaseStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-key}") String serviceKey,
            @Value("${supabase.storage.bucket}") String bucket,
            @Value("${app.upload.dir:${user.home}/.data-labeling/uploads}") String uploadDir,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.supabaseUrl = supabaseUrl != null ? supabaseUrl : "";
        this.serviceKey = serviceKey != null ? serviceKey : "";
        this.bucket = bucket != null ? bucket : "data-items";
        // Luôn dùng đường dẫn tuyệt đối để tránh FileNotFoundException khi chạy từ thư mục tạm Tomcat
        String dir = uploadDir != null && !uploadDir.isBlank() ? uploadDir : System.getProperty("user.home") + "/.data-labeling/uploads";
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize().toString();
        this.baseUrl = baseUrl != null ? baseUrl.replaceAll("/$", "") : "http://localhost:8080";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Upload file: nếu Supabase chưa cấu hình (url chứa "placeholder") thì lưu local.
     * Nếu đã cấu hình Supabase thì thử gửi lên Supabase; nếu thất bại thì fallback lưu local.
     */
    public String upload(MultipartFile file, String folder) {
        if (useLocalStorage()) {
            return saveLocally(file, folder);
        }
        try {
            return uploadToSupabase(file, folder);
        } catch (Exception e) {
            // Supabase lỗi (key sai, bucket chưa tạo, mạng...) → lưu local để upload vẫn dùng được
            return saveLocally(file, folder);
        }
    }

    private boolean useLocalStorage() {
        if (supabaseUrl == null) return true;
        String u = supabaseUrl.trim();
        return u.isEmpty() || u.contains(SUPABASE_PLACEHOLDER);
    }

    private String saveLocally(MultipartFile file, String folder) {
        String safeName = sanitizeFilename(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + safeName;
        Path dir = Paths.get(uploadDir, folder).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(storedName);
            file.transferTo(target);
            return baseUrl + "/api/uploads/" + folder + "/" + storedName;
        } catch (IOException e) {
            throw new BadRequestException("Lưu file thất bại: " + (e.getMessage() != null ? e.getMessage() : "Lỗi ghi đĩa."));
        }
    }

    private String uploadToSupabase(MultipartFile file, String folder) {
        String safeName = sanitizeFilename(file.getOriginalFilename());
        String objectPath = folder + "/" + UUID.randomUUID() + "_" + safeName;
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + objectPath;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .header("x-upsert", "true")
                    .header("Content-Type", contentTypeOrDefault(file))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                String body = response.body();
                String detail = (body != null && !body.isBlank()) ? body : ("HTTP " + status + " – kiểm tra cấu hình Supabase (url, service-key, bucket).");
                throw new BadRequestException("Upload thất bại: " + detail);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Upload thất bại: " + (e.getMessage() != null ? e.getMessage() : "Đã bị gián đoạn."));
        } catch (IOException e) {
            throw new BadRequestException("Upload thất bại: " + (e.getMessage() != null ? e.getMessage() : "Lỗi đọc/ghi file."));
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + objectPath;
    }

    private String sanitizeFilename(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }
        return Paths.get(original).getFileName().toString().replaceAll("\\s+", "_");
    }

    private String contentTypeOrDefault(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null ? contentType : "application/octet-stream";
    }
}
