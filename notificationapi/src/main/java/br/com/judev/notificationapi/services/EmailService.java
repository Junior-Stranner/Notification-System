package br.com.judev.notificationapi.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public boolean sendMail() {
        ZonedDateTime dataHoraBrasil = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");
        String dataFormatada = dataHoraBrasil.format(formatter);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = getMimeMessageHelper(message, dataFormatada);

            // Carregar o GIF do classpath
            ClassPathResource gifFile = new ClassPathResource("static/Happy-Jonah-Hill.gif");
            if (!gifFile.exists()) {
                logger.error("O arquivo Happy-Jonah-Hill.gif não foi encontrado!");
                return false;
            }

            helper.addInline("jonahGif", gifFile);

            mailSender.send(message);

            logger.info("Email enviado com sucesso em {}", dataFormatada);
            return true;
        } catch (Exception e) {
            logger.error("Erro ao enviar email", e);
            return false;
        }
    }

    private MimeMessageHelper getMimeMessageHelper(MimeMessage message, String dataFormatada) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String content = """
            <html>
              <body style="font-family: Arial, sans-serif; text-align: center; background-color: #f9f9f9; padding: 20px;">
                <h2 style="color: #333;">🎉 Você recebeu uma <b>nova visita</b>!</h2>
                <p style="color: #555;">Momento da visita:</p>
                <p style="color: #000;"><b>%s</b></p>
                <img src="cid:jonahGif" width="300" alt="Happy Jonah Hill" style="border-radius: 10px;"/>
                <p style="margin-top: 20px; color: #555;">Obrigado por visitar meu portfólio!</p>
              </body>
            </html>
            """.formatted(dataFormatada);

        helper.setFrom(fromEmail);
        helper.setTo(fromEmail);
        helper.setSubject("🚀 Você tem uma nova visita!");
        helper.setText(content, true);
        return helper;
    }
}
