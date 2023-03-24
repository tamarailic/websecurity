package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.services.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @PostMapping
    public Certificate createNew() {
        return certificateService.createNewCertificate();
    }
}
