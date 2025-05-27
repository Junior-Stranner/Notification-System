package br.com.judev.notificationapi.services;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final AtomicReference<Instant> lastEmailTime = new AtomicReference<>(Instant.MIN);

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public boolean sendMail() {
        Instant now = Instant.now();
        Instant lastTime = lastEmailTime.get();

        if (Duration.between(lastTime, now).getSeconds() < 10) {
            logger.warn("Email não enviado. Último envio foi há menos de 10 segundos.");
            return false;
        }

        if (!lastEmailTime.compareAndSet(lastTime, now)) {
            logger.warn("Outro envio de email está em andamento.");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            String content = """
                    <html>
                      <body>
                        <p>Você recebeu uma nova visita!</p>
                        <p>Momento da visita: %s</p>
                        <img src="https://yourdomain.com/api/track/image" width="1" height="1" style="display:none;" alt="">
                      </body>
                    </html>
                    """.formatted(now);

            helper.setFrom(fromEmail);
            helper.setTo(fromEmail);
            helper.setSubject("Você tem uma nova visita!");
            helper.setText(content, true);

            mailSender.send(message);

            logger.info("Email enviado com sucesso em {}", now);
            return true;
        } catch (Exception e) {
            logger.error("Erro ao enviar email", e);
            return false;
        }
    }
}
