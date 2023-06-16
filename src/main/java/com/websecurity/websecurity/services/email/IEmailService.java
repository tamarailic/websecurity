package com.websecurity.websecurity.services.email;

import com.websecurity.websecurity.models.User;

public interface IEmailService {
    void sendText(String to, String subject, String body);

    void sendHTML(String to, String subject, String body);

    void sendVerificationEmail(User user, String url);

    void sendPasswordChangeEmail(User user, String code);

    void send2FAEmail(User user, String code);
}
