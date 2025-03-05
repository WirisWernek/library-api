package io.github.wiriswernek.library_api.service.imp;

import io.github.wiriswernek.library_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {

    @Value("${spring.application.mail.lateloans.default-remetent}")
    private String remetente;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String message, List<String> emails) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        var to = emails.toArray(new String[emails.size()]);

        mailMessage.setFrom(remetente);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(to);

        javaMailSender.send(mailMessage);

    }
}
