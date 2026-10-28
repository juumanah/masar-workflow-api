package com.jumanah.masar;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  
    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Map.of(
            "message", "Welcome to Masar!",
            "project", "Service Request & Approval System"
        );
    }
}
