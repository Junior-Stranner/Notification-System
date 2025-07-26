package br.com.judev.notificationapi.controller;

import br.com.judev.notificationapi.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);


    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping("/notify")
    public ResponseEntity<String> notifyAccess() {
        logger.info("Requisição recebida para enviar notificação.");
        boolean sent = emailService.sendMail();
        if (sent) {
            return ResponseEntity.ok("Email enviado com sucesso.");
        } else {
            return ResponseEntity.status(429).body("Aguarde antes de enviar novamente.");
        }
    }
}

