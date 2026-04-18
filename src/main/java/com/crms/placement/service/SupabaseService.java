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

    private final String BUCKET = "resumes";

    public SupabaseService(SupabaseConfigService config) {
        this.config = config;
    }

    public String uploadResume(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            String supabaseUrl = config.getSupabaseUrl();
            String apiKey = config.getApiKey();

            String uploadUrl =
                    supabaseUrl + "/storage/v1/object/" + BUCKET + "/" + fileName;

            // upload file
            webClient.post()
                    .uri(uploadUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // return public URL
            return supabaseUrl
                    + "/storage/v1/object/public/"
                    + BUCKET + "/"
                    + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Resume upload failed", e);
        }
    }
}