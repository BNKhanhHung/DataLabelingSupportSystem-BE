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

/**
 * Dịch vụ upload file: ưu tiên Supabase Storage khi cấu hình hợp lệ; nếu chưa cấu hình
 * (URL placeholder), lỗi mạng hoặc HTTP lỗi từ Supabase thì lưu xuống đĩa cục bộ dưới
 * {@code app.upload.dir} và trả URL phục vụ qua {@link UploadController}.
 * <p>
 * Các tham số: {@code supabase.url}, {@code supabase.service-key}, {@code supabase.storage.bucket},
 * {@code app.upload.dir}, {@code app.base-url}.
 */
@Service
public class SupabaseStorageService {

    private static final String SUPABASE_PLACEHOLDER = "placeholder.supabase.co";

    private final String supabaseUrl;
    private final String serviceKey;
    private final String bucket;
    private final String uploadDir;
    private final String baseUrl;
    private final HttpClient httpClient;

    /**
     * Khởi tạo client HTTP, chuẩn hóa đường dẫn upload tuyệt đối và {@code baseUrl} (bỏ slash cuối).
     *
     * @param supabaseUrl URL dự án Supabase
     * @param serviceKey  service role key (Bearer + apikey)
     * @param bucket      tên bucket storage
     * @param uploadDir   thư mục lưu local (mặc định dưới user home nếu trống)
     * @param baseUrl     URL gốc ứng dụng để ghép link file local
     */
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
     * Upload file: nếu Supabase chưa cấu hình (URL chứa placeholder) thì chỉ lưu local.
     * Nếu đã cấu hình thì gọi API Storage; mọi exception được nuốt và fallback lưu local để
     * luồng upload không chết hoàn toàn khi Supabase tạm lỗi.
     *
     * @param file   multipart từ request
     * @param folder thư mục con (prefix object path / thư mục trên đĩa)
     * @return URL công khai (Supabase public object) hoặc URL ứng dụng {@code /api/uploads/...}
     * @throws BadRequestException nếu ghi local thất bại (IOException)
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

    /**
     * @return {@code true} khi không có URL hoặc URL vẫn là placeholder (dev / chưa cấu hình)
     */
    private boolean useLocalStorage() {
        if (supabaseUrl == null) return true;
        String u = supabaseUrl.trim();
        return u.isEmpty() || u.contains(SUPABASE_PLACEHOLDER);
    }

    /**
     * Ghi file vào {@code uploadDir/folder} với tên {@code UUID_originalSanitized}.
     *
     * @param file   nội dung upload
     * @param folder thư mục con an toàn (không chứa .. — caller chịu trách nhiệm)
     * @return URL tuyệt đối tới endpoint serve file
     * @throws BadRequestException khi tạo thư mục/transfer thất bại
     */
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

    /**
     * PUT object lên Supabase Storage (upsert), trả URL public sau khi thành công.
     *
     * @param file   multipart
     * @param folder prefix thư mục trong bucket
     * @return URL public Supabase
     * @throws BadRequestException khi HTTP không 2xx, interrupted, hoặc IOException đọc bytes
     */
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

    /**
     * Lấy tên file cuối, thay khoảng trắng bằng {@code _}; null/blank → {@code "file"}.
     */
    private String sanitizeFilename(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }
        return Paths.get(original).getFileName().toString().replaceAll("\\s+", "_");
    }

    /**
     * @return {@code Content-Type} của multipart hoặc {@code application/octet-stream}
     */
    private String contentTypeOrDefault(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null ? contentType : "application/octet-stream";
    }
}
