package com.nracademy.backend.service.impl;

import com.nracademy.backend.dto.response.UploadResponse;
import com.nracademy.backend.entity.enums.StatusCode;
import com.nracademy.backend.exception.common.MissingRequiredFieldException;
import com.nracademy.backend.exception.common.StorageDeleteFailedException;
import com.nracademy.backend.exception.common.StorageUploadFailedException;
import com.nracademy.backend.exception.common.UnsupportedMediaTypeException;
import com.nracademy.backend.exception.common.ValidationFailedException;
import com.nracademy.backend.service.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
<<<<<<< Updated upstream
public class S3ServiceImpl implements S3Service{
=======
public class S3ServiceImpl implements S3Service {
>>>>>>> Stashed changes

    final S3Client s3Client;

    @Value("${aws.bucket-name:}")
    String bucket;

    @Value("${aws.endpoint-url:}")
    String endpointUrl;

    @Value("${aws.bucket-prefix:rent_a_car/uploads}")
    String bucketPrefix;
    String carImagePrefix;
    String userImagePrefix;
    String blogPostImagePrefix;
    String podcastAudioPrefix;
    String settingImagePrefix;
    @Value("${aws.default-region:eu-central-1}")
    String region;

    @Value("${file.image.max-size:3145728}")
    long maxImageFileSize;

    @Value("${file.audio.max-size:52428800}")
    long maxAudioFileSize;

    @Value("${public.domain:}")
    String publicUrl;

    @PostConstruct
    public void initPrefixes() {
        bucketPrefix = trimTrailingSlash(bucketPrefix);
        carImagePrefix = bucketPrefix + "/images/cars";
        userImagePrefix = bucketPrefix + "/images/users";
        blogPostImagePrefix = bucketPrefix + "/images/blogs";
        podcastAudioPrefix = bucketPrefix + "/audios/podcasts";
        settingImagePrefix = bucketPrefix + "/images/settings";
    }

    private String trimTrailingSlash(String s) {
        if (s == null || s.isBlank()) return "rent_a_car/uploads";
        s = s.trim();
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    public UploadResponse uploadSettingImageFile(MultipartFile file) {
        validateFileEmpty(file);
        validateImageFile(file);
        validateMaxFileSize(file, maxImageFileSize);
        return upload(file, settingImagePrefix);
    }

    public UploadResponse uploadCarImageFile(MultipartFile file) {
        validateFileEmpty(file);
        validateImageFile(file);
        validateMaxFileSize(file, maxImageFileSize);
        return upload(file, carImagePrefix);
    }

    public UploadResponse uploadUserImageFile(MultipartFile file) {
        validateFileEmpty(file);
        validateImageFile(file);
        validateMaxFileSize(file, maxImageFileSize);
        return upload(file, userImagePrefix);
    }

    public UploadResponse uploadBlogPostCoverImageFile(MultipartFile file) {
        validateFileEmpty(file);
        validateImageFile(file);
        validateMaxFileSize(file, maxImageFileSize);
        return upload(file, blogPostImagePrefix);
    }

    public UploadResponse uploadPodcastAudioFile(MultipartFile file) {
        validateFileEmpty(file);
        validateAudioFile(file);
        validateMaxFileSize(file, maxAudioFileSize);
        return upload(file, podcastAudioPrefix);
    }

    private UploadResponse upload(MultipartFile file, String prefix) {
        validateBucket(false);
        String contentType = normalizeContentType(file.getContentType());
        String key = buildKey(contentType, prefix);
        byte[] bytes = readFileBytes(file);
        PutObjectRequest request = buildPutObjectRequest(key, contentType, bytes.length);
        putObject(request, bytes);
        String url = buildPublicUrl(key);
        return new UploadResponse(key, url, bytes.length);
    }

    private void putObject(PutObjectRequest request, byte[] bytes) {
        try {
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            throw new StorageUploadFailedException(
                    "Failed to upload to S3: " + e.getMessage(),
                    StatusCode.STORAGE_UPLOAD_FAILED,
                    List.of());
        }
    }

    private byte[] readFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new StorageUploadFailedException(
                    "Failed to read upload bytes: " + e.getMessage(),
                    StatusCode.STORAGE_UPLOAD_FAILED,
                    List.of());
        }
    }

    private PutObjectRequest buildPutObjectRequest(String key, String contentType, long length) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(length)
                .build();
    }

    private void validateFileEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageUploadFailedException(
                    "File is empty",
                    StatusCode.STORAGE_UPLOAD_FAILED,
                    List.of());
        }
        String contentType = file.getContentType() != null ? file.getContentType() : "";
        if (contentType == null || contentType.isBlank())
            throw new MissingRequiredFieldException(
                    "File content type is not specified",
                    StatusCode.MISSING_REQUIRED_FIELD,
                    List.of());
    }

    public void delete(String key) {
        validateBucket(true);
        validateKeyEmpty(key);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new StorageDeleteFailedException(
                    "Failed to delete from S3: " + e.getMessage(),
                    StatusCode.STORAGE_DELETE_FAILED,
                    List.of());
        }
    }

    private void validateKeyEmpty(String key) {
        if (key == null || key.isBlank()) {
            throw new StorageDeleteFailedException(
                    "Key is required",
                    StatusCode.STORAGE_DELETE_FAILED,
                    List.of());
        }
    }

    private String buildPublicUrl(String key) {
        if (publicUrl != null && !publicUrl.isBlank()) {
            return trimTrailingSlash(publicUrl) + "/" + key;
        }
        return "https://" + bucket + ".s3." + region.trim() + ".amazonaws.com/" + key;
    }

    private String getExtension(String contentType) {
        if (contentType == null)
            return "";

        return switch (contentType) {
            case "application/pdf" -> ".pdf";
            case "application/msword" -> ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }

    private void validateImageFile(MultipartFile file) {
        List<String> allowedContentTypes = List.of(
                "image/png",
                "image/webp",
                "image/jpeg");

        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new UnsupportedMediaTypeException(
                    "Invalid file type. Only PNG, WEBP, and JPEG images are allowed",
                    StatusCode.UNSUPPORTED_MEDIA_TYPE,
                    List.of());
        }
    }

    private void validateAudioFile(MultipartFile file) {
        List<String> allowedContentTypes = List.of(
                "audio/mpeg",
                "audio/mp3",
                "audio/wav",
                "audio/x-wav",
                "audio/ogg",
                "audio/aac",
                "audio/mp4",
                "audio/x-m4a");

        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new UnsupportedMediaTypeException(
                    "Invalid audio type. Allowed: MP3, WAV, OGG, AAC, M4A",
                    StatusCode.UNSUPPORTED_MEDIA_TYPE,
                    List.of());
        }
    }

    private void validateMaxFileSize(MultipartFile file, long maxFileSize) {
        if (file.getSize() > maxFileSize) {
            throw new ValidationFailedException(
                    "File size exceeds the limit",
                    StatusCode.VALIDATION_FAILED,
                    List.of());
        }
    }

    private void validateBucket(boolean forDelete) {
        if (bucket != null && !bucket.isBlank())
            return;

        if (forDelete) {
            throw new StorageDeleteFailedException(
                    "AWS bucket is not configured (aws.bucket-name / AWS_BUCKET_NAME)",
                    StatusCode.STORAGE_DELETE_FAILED,
                    List.of());
        }

        throw new StorageUploadFailedException(
                "AWS bucket is not configured (aws.bucket-name / AWS_BUCKET_NAME)",
                StatusCode.STORAGE_UPLOAD_FAILED,
                List.of());
    }

    private String buildKey(String contentType, String prefix) {
        String extension = getExtension(normalizeContentType(contentType));
        String id = UUID.randomUUID().toString();
        String cleanPrefix = (prefix == null || prefix.isBlank()) ? "uploads" : prefix.trim();
        if (extension.isBlank())
            return cleanPrefix + "/" + id;
        return cleanPrefix + "/" + id + extension;
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank())
            return "application/octet-stream";
        return contentType.trim();
    }
}