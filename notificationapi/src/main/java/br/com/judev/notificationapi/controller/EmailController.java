package br.com.judev.notificationapi.controller;

import br.com.judev.notificationapi.dto.EmailNotificationResult;
import br.com.judev.notificationapi.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Envia email de notificação síncrono.
     */
    @Operation(summary = "Enviar notificação por email (síncrona)")
    @PostMapping("/notify")
    public ResponseEntity<EmailNotificationResult> notifyAccess(
            @RequestParam(required = false) String customMessage) {
        String message = (customMessage != null) ? customMessage : "Alguém acessou seu portfólio!";
        try {
            EmailNotificationResult result = emailService.sendNotification(
                    "🚀 Nova visita ao portfólio",
                    message);
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new EmailNotificationResult(false, "Erro interno ao enviar email."));
        }
    }

    /**
     * Envia email de notificação assíncrona.
     */
    @Operation(summary = "Enviar notificação por email (assíncrona)")
    @PostMapping("/async-notify")
    public CompletableFuture<ResponseEntity<EmailNotificationResult>> notifyAccessAsync(
            @RequestParam(required = false) String customMessage) {
        String message = (customMessage != null) ? customMessage : "Alguém acessou seu portfólio!";
        return emailService.sendNotificationAsync(
                        "🚀 Nova visita ao portfólio", message)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return ResponseEntity.ok(result);
                    }
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(result);
                })
                .exceptionally(ex ->
                        ResponseEntity.internalServerError()
                                .body(new EmailNotificationResult(false, "Erro interno ao enviar email (assíncrono)."))
                );
    }
}
