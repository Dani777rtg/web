package com.ucaldas.electoral.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(MailNotificationService.class);

    private final JavaMailSender mailSender;

    public MailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Código de verificación - Sistema electoral UCaldas");
            msg.setText("Tu código de verificación es: " + code + "\nExpira en 5 minutos.");
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("No se pudo enviar correo a {}: {}. Código (solo dev): {}", to, e.getMessage(), code);
        }
    }
}
