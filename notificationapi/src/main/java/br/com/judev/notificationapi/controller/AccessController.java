package br.com.judev.notificationapi.controller;

import br.com.judev.notificationapi.dto.AccessResponse;
import br.com.judev.notificationapi.services.EmailService;
import br.com.judev.notificationapi.services.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
public class AccessController {
    private final TrackingService trackingService;
    private final EmailService emailService;
    ;

    public AccessController(TrackingService trackingService, EmailService emailService) {
        this.trackingService = trackingService;
        this.emailService = emailService;
    }

    /**
     * Registra acesso e envia notificação por e-mail
     */
    @GetMapping
    public ResponseEntity<AccessResponse> registerAccess(HttpServletRequest request, HttpServletResponse response){
        // 1. Identifica o visitante (cookie + IP)
        String visitorKey = trackingService.identifyVisitor(request, response);

        // 2. Registra o acesso (contagem em memória/arquivo)
        int accessCount = trackingService.registerAccess(visitorKey);

        // 3. Envia e-mail de notificação
        emailService.sendNotification(
                "Novo acesso detectado",
                "Visitante: " + visitorKey + "\nTotal de acessos: " + accessCount
        );
        return ResponseEntity.ok(
                new AccessResponse(
                        visitorKey.split("\\|")[0], // ID do cookie
                        request.getRemoteAddr(),    // IP
                        accessCount                 // Contagem
                )
        );
    }
}

