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
@RequestMapping("/api/v1/access")
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

        String visitorKey = trackingService.identifyVisitor(request, response);
        int accessCount = trackingService.registerAccess(visitorKey);

// Mensagem simplificada
        String emailBody = """
    <strong>Detalhes do acesso:</strong>
    <ul>
        <li>ID: %s</li>
        <li>Total de acessos: %d</li>
        <li>User-Agent: %s</li>
    </ul>
    """.formatted(
                visitorKey,
                accessCount,
                request.getHeader("User-Agent")
        );

        emailService.sendNotification(
                "👤 Novo acesso ao portfólio",
                emailBody
        );
        return ResponseEntity.ok(
                new AccessResponse(
                        visitorKey,       // apenas o ID do cookie// pode retornar null ou remover o campo IP do DTO
                        accessCount
                )
        );
    }
}

