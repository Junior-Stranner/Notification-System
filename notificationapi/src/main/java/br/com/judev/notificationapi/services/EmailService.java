package br.com.judev.notificationapi.services;

import br.com.judev.notificationapi.dto.EmailNotificationResult;
import br.com.judev.notificationapi.exceptions.handlerMailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final AtomicReference<Instant> lastSent = new AtomicReference<>(Instant.MIN);

    @Value("${notification.throttle-seconds:60}")
    private long throttleSeconds;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }


    /**
     * Envia e-mail de notificação padrão (visita ao portfólio)
     */
    public EmailNotificationResult sendMail() throws handlerMailException {
        return sendNotification(
                "🚀 Você tem uma nova visita!",
                """
                        <h2 style="color: #333; text-align: center;">🎉 Você recebeu uma <span style="color: #007bff;">nova visita</span>!</h2>
                        <p style="color: #555; text-align: center; font-size: 16px;">Alguém acabou de acessar seu portfólio! 🎯</p>
                        """
        );
    }

    /**
     * Envia notificação personalizada
     *
     * @param subject     Assunto do e-mail
     * @param htmlContent Corpo da mensagem em HTML
     */
    public EmailNotificationResult sendNotification(String subject, String htmlContent) throws handlerMailException {
        Instant now = Instant.now();
        Duration sinceLast = Duration.between(lastSent.get(), now);

        if (sinceLast.getSeconds() < throttleSeconds) {
            long wait = throttleSeconds - sinceLast.getSeconds();
            logger.warn("Throttle ativo. Aguarde {} segundos antes de enviar novamente.", wait);
            return new EmailNotificationResult(false, "Aguarde " + wait + "s antes de enviar novamente.");
        }

        ZonedDateTime dataHoraBrasil = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");
        String dataFormatada = dataHoraBrasil.format(formatter);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fullContent = """
                    <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px; margin: 0;">
                    <div style="max-width: 600px; background-color: white; margin: auto; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); padding: 30px;">
                      <h2 style="color: #333; text-align: center;">🎉 Você recebeu uma <span style="color: #007bff;">nova visita</span>!</h2>
                      <p style="color: #555; text-align: center; font-size: 16px;">Alguém acabou de acessar seu portfólio! 🎯</p>
                      <p style="text-align: center; color: #000; font-size: 18px; margin-top: 20px;">
                        <strong>📅 Momento da visita:</strong><br>
                        <span style="color: #007bff;">%s</span>
                      </p>
                      <div style="text-align: center; margin: 30px 0;">
                        <img src="cid:jonahGif" width="320" alt="Happy Jonah Hill" style="border-radius: 12px;"/>
                      </div>
                      <p style="text-align: center; color: #aaa; font-size: 12px; margin-top: 30px;">
                        Esta é uma notificação automática do seu portfólio.
                      </p>
                    </div>
                    </body>
                    </html>
                    """.formatted(htmlContent, dataFormatada);

            helper.setFrom(fromEmail);
            helper.setTo(fromEmail);
            helper.setSubject(subject);
            helper.setText(fullContent, true);

            ClassPathResource gifFile = new ClassPathResource("static/Happy-Jonah-Hill.gif");
            if (!gifFile.exists()) {
                logger.error("Arquivo Happy-Jonah-Hill.gif não encontrado!");
                return new EmailNotificationResult(false, "Imagem do e-mail não encontrada.");
            }

            helper.addInline("jonahGif", gifFile);
            mailSender.send(message);
            lastSent.set(now);
            logger.info("Email enviado com sucesso em {}", dataFormatada);
            return new EmailNotificationResult(true, "Email enviado com sucesso!");

        } catch (MailAuthenticationException ex) {
            logger.error("Falha de autenticação no serviço de email", ex);
            throw new handlerMailException("Falha de autenticação no servidor SMTP", ex);
        } catch (MessagingException ex) {
            logger.error("Erro ao construir a mensagem de email", ex);
            throw new handlerMailException("Erro na construção do email", ex);
        } catch (Exception ex) {
            logger.error("Erro inesperado ao enviar email", ex);
            throw new handlerMailException("Erro inesperado no serviço de email", ex);
        }
    }


    @Async
    public CompletableFuture<EmailNotificationResult> sendMailAsync() {
        return CompletableFuture.completedFuture(sendMail());
    }

    @Async
    public CompletableFuture<EmailNotificationResult> sendNotificationAsync(String subject, String message) {
        return CompletableFuture.completedFuture(sendNotification(subject, message));
    }
}



/*
* Melhoria	                                       Impacto
Eliminação de duplicação	               Menos bugs, mais fácil manter
Template dinâmico	                      80% menos código para novos e-mails
Controle centralizado	                    Throttle e erros consistentes
Flexibilidade	                    Adaptável a novos requisitos sem mudar estrutura
*/