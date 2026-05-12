package com.erp.services.implemented;

import com.erp.services.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImplement implements MailService {


    private final JavaMailSender javaMailSender;

    //for sending mail in another thread
    @Async
    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String fileName) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // true flag indicates multipart message for attachment
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            // Adding the Jasper PDF attachment
            helper.addAttachment(fileName, new ByteArrayResource(attachment));

            javaMailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed");
        }
    }

    //for sending employee email*****************
    @Override
    @Async
    public void sendSimpleEmail(
            String to,
            String subject,
            String body
    ) {

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);

            log.info("Simple email sent to {}", to);

        } catch (Exception e) {

            log.error("Failed to send email {}", e.getMessage());

            throw new RuntimeException("Email failed");
        }
    }

}