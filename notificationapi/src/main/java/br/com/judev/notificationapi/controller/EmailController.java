package br.com.judev.notificationapi.controller;

import br.com.judev.notificationapi.dto.EmailNotificationResult;
import br.com.judev.notificationapi.services.EmailService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }
        @PostMapping("/notify")
        public ResponseEntity<EmailNotificationResult> notifyAcess(@RequestParam (required = false) String customMessage){
            String message = customMessage != null ? customMessage : "Alguém acessou seu portfólio!";

            try {
                EmailNotificationResult result = emailService.sendNotification(
                        "🚀 Nova visita ao portfólio",
                        message
                );
                return createResponse(result);
            } catch (Exception e) {
                logger.error("Falha no envio de notificação", e);
                return ResponseEntity.internalServerError()
                        .body(new EmailNotificationResult(false, "Erro interno"));
            }
    }


    @PostMapping("/async-notify")
    public CompletableFuture<ResponseEntity<EmailNotificationResult>> notifyAccessAsync() {
        logger.info("Requisição recebida para enviar notificação assíncrona");
        return emailService.sendNotificationAsync(
                "🚀 Nova visita ao portfólio",
                        "Alguém acessou seu portfólio!")
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        logger.info("Email enviado com sucesso via chamada assíncrona");
                        return ResponseEntity.ok(result);
                    }
                    logger.warn("Throttle ativo via chamada assíncrona");
                    return ResponseEntity.status(429).body(result);
                })
                .exceptionally(ex -> {
                    logger.error("Falha no envio assíncrono", ex);
                    return ResponseEntity.internalServerError()
                            .body(new EmailNotificationResult(false, "Erro interno no servidor"));
                });
    }
}