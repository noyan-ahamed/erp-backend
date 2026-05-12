package com.erp.services;

public interface MailService {
    void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String fileName);

    void sendSimpleEmail(String email, String subject, String body);
}
