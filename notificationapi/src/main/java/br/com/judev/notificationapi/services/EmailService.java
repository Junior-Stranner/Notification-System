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
<body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 40px; margin: 0;">
<div style="max-width: 600px; background-color: white; margin: auto; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); padding: 30px;">
  
  <h2 style="color: #333; text-align: center;">🎉 Você recebeu uma <span style="color: #007bff;">nova visita</span>!</h2>
  <p style="color: #555; text-align: center; font-size: 16px;">
    Alguém acabou de acessar seu portfólio! 🎯
  </p>
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
""".formatted(dataFormatada);


        helper.setFrom(fromEmail);
        helper.setTo(fromEmail);
        helper.setSubject("🚀 Você tem uma nova visita!");
        helper.setText(content, true);
        return helper;
    }
}
