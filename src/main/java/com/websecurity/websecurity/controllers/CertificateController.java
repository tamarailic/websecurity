package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.services.ICertificateGeneratorService;
import com.websecurity.websecurity.services.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    ICertificateService certificateService;
    @Autowired
    private ICertificateGeneratorService certificateGeneratorService;


    @PostMapping("/request/{userId}")
    public CertificateRequest createCertificateRequestUser(@PathVariable Long userId,
                                                           @RequestBody CertificateRequest certificateRequest) {
        return certificateService.createCertificateRequest(userId, certificateRequest);
    }

    @PutMapping("/approve/{requestId}")
    public Certificate approveRequest(@PathVariable Long requestId) {
        return certificateGeneratorService.createNewCertificate(requestId);
    }

    @PutMapping("/deny/{requestId}")
    public void denyRequest(@PathVariable Long requestId) {

    }

}
