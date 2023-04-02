package com.websecurity.websecurity;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.security.Security;

@SpringBootApplication
@EnableMongoRepositories
public class WebsecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsecurityApplication.class, args);
        Security.addProvider(new BouncyCastleProvider());
    }

}
