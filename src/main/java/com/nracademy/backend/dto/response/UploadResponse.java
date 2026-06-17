package com.nracademy.backend.dto.response;

public record UploadResponse(
    String key,
    String url,
    long size
) {}