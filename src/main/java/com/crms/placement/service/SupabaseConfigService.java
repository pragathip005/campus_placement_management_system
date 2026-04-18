package com.crms.placement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SupabaseConfigService {
    @Value("${supabase.api-key}")
    private String apiKey;
    @Value("${supabase.url}")
    private String supabaseUrl;
    public String getApiKey() {
        return apiKey;
    }
    public String getSupabaseUrl() {
        return supabaseUrl;
    }
}