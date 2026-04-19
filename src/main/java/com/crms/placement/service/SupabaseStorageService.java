package com.crms.placement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.apikey}")
    private String supabaseKey;

    private final String SUPABASE_PROJECT_URL = "https://dgqvsehwglhisstoogus.supabase.co";
    private final String BUCKET_NAME = "job-descriptions";

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        // Generate a unique file name to prevent overwriting
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        String uploadUrl = SUPABASE_PROJECT_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseKey);
        headers.set("Content-Type", file.getContentType() != null ? file.getContentType() : "application/pdf");

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // Return the public URL
            return SUPABASE_PROJECT_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
        } else {
            throw new RuntimeException("Failed to upload file to Supabase: " + response.getBody());
        }
    }
}
