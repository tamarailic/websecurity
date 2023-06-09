package com.websecurity.websecurity.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class TestController {
    @Autowired
    EmailService sendGridEmailService;

    @GetMapping
    public void testSending() {
        this.sendGridEmailService.sendHTML("urke.mocni@gmail.com", "Hello World", "Hello, <strong>how are you doing?</strong>");
    }
}
