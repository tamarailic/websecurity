package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.services.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @PostMapping
    public Certificate createNew() {
        return certificateService.createNewCertificate();
    }

    @PostMapping("/request")
    public CertificateRequest createCertificateRequestUser(@RequestParam(name = "id") Long userId,
            @RequestBody CertificateRequest certificateRequest){
            return certificateService.createCertificateRequest(userId, certificateRequest);
    }
}
