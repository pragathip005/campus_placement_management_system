package com.crms.placement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Backend is running 🚀";
    }

    @GetMapping("/test")
    public String test() {
        return "Everything working properly ✅";
    }
}