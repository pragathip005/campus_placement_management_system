package com.crms.placement.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class SupabaseService {

    private final SupabaseConfigService config;
    private final WebClient webClient = WebClient.builder().build();

    // keeping BOTH bucket flexibility from 1st + 2nd logic
    private final String RESUME_BUCKET = "resumes";
    private final String JD_BUCKET = "job-descriptions";

    public SupabaseService(SupabaseConfigService config) {
        this.config = config;
    }

    // you can choose bucket dynamically now
    public String uploadFile(MultipartFile file, boolean isJobDescription) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Cannot upload an empty file");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;

            String bucket = isJobDescription ? JD_BUCKET : RESUME_BUCKET;

            String supabaseUrl = config.getSupabaseUrl();
            String apiKey = config.getApiKey();

            String uploadUrl =
                    supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            webClient.post()
                    .uri(uploadUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return supabaseUrl
                    + "/storage/v1/object/public/"
                    + bucket + "/"
                    + fileName;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    // optional: keep old method name for backward compatibility
    public String uploadResume(MultipartFile file) {
        return uploadFile(file, false);
    }

    public String uploadJobDescription(MultipartFile file) {
        return uploadFile(file, true);
    }
}