package com.websecurity.websecurity.services.email;

public interface IEmailService {
    void sendText(String to, String subject, String body);

    void sendHTML(String to, String subject, String body);
}
