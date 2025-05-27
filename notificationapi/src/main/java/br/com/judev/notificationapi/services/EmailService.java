package br.com.judev.notificationapi.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private Instant lastEmailTime = Instant.MIN;
    private final ReentrantLock lock = new ReentrantLock();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    void sendMail() {
        if (lock.tryLock()) {
            try {
                Instant now = Instant.now();
                if (Duration.between(lastEmailTime, now).getSeconds() < 10) {
                    System.out.println("Email não enviado (menos de 10s desde o último envio)");
                    return;
                }
                lastEmailTime = now;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                String content = """
                    <html>
                      <body>
                        <p>MENSAGEM DO CORPO DO EMAIL</p>
                        <p>Momento da visita: %s</p>
                        <img src="">
                      </body>
                    </html>
                    """.formatted(now);

                helper.setFrom("SEU_EMAIL@gmail.com");
                helper.setTo("DESTINO@gmail.com");
                helper.setSubject("Você tem uma nova visita!");
                helper.setText(content, true);

                mailSender.send(message);

                System.out.println("Email enviado em " + now);
            } catch (Exception e) {
                System.out.println("Erro ao enviar email: " + e.getMessage());
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Outro envio está em andamento.");
        }
    }

}

