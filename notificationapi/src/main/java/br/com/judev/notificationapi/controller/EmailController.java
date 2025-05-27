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
        boolean sent = emailService.sendMail();
        if (sent) {
            return ResponseEntity
                    .ok("Visita registrada e e‑mail enviado com sucesso!");
        } else {
            return ResponseEntity
                    .status(429)
                    .body("E‑mail já enviado recentemente. Aguarde alguns segundos.");
        }
    }
}

