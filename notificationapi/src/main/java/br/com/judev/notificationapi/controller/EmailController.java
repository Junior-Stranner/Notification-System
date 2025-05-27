package br.com.judev.notificationapi.controller;

import br.com.judev.notificationapi.services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/track-visit")
    public ResponseEntity<String> trackVisit() {
        emailService.sendMail();
        return ResponseEntity.ok("Visit tracked");
    }
}

