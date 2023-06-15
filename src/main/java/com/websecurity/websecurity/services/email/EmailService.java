package com.websecurity.websecurity.services.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.services.HelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailService implements IEmailService {

    @Autowired
    private SendGrid sendGridClient;
    @Autowired
    private HelperService helperService;


    @Override
    public void sendText(String to, String subject, String body) {
        Response response = sendEmail(to, subject, new Content("text/plain", body));
    }

    @Override
    public void sendHTML(String to, String subject, String body) {
        Response response = sendEmail(to, subject, new Content("text/html", body));
    }

    private Response sendEmail(String to, String subject, Content content) {
        String from = helperService.getEmailFrom();
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email(from));
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = this.sendGridClient.api(request);
        } catch (IOException ex) {

        }
        return response;
    }


    @Override
    public void sendVerificationEmail(User user, String url) {
        String toAddress = user.getUsername();

        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "WEBSECURITY";

        content = content.replace("[[name]]", user.getFirstName());
        content = content.replace("[[URL]]", url);
        sendHTML(toAddress, subject, content);
    }

    @Override
    public void sendPasswordChangeEmail(User user, String code) {
        String toAddress = user.getUsername();

        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Your password change code is:<br>"
                + "<h3>[[code]]</h3>"
                + "Thank you,<br>"
                + "WEBSECURITY";
        content = content.replace("[[name]]", user.getFirstName());
        content = content.replace("[[code]]", code);
        sendHTML(toAddress, subject, content);
    }

    @Override
    public void send2FAEmail(User user, String code) {
        String toAddress = user.getUsername();

        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Your 2FA code is:<br>"
                + "<h3>[[code]]</h3>"
                + "Thank you,<br>"
                + "Shuttle";

        content = content.replace("[[name]]", user.getFirstName());
        content = content.replace("[[code]]", code);
        sendHTML(toAddress, subject, content);
    }

}
