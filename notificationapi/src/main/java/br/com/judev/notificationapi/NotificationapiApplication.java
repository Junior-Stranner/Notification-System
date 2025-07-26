package br.com.judev.notificationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NotificationapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationapiApplication.class, args);
    }

}
