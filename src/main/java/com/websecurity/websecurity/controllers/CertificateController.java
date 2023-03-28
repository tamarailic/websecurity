package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.services.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;


    @PostMapping("/request/user/{userId}")
    public CertificateRequestDTO createCertificateRequestUser(@PathVariable Long userId,
                                                              @RequestBody CertificateRequestDTO certificateRequestDTO) {
        return certificateService.createCertificateRequestForUser(userId, certificateRequestDTO);
    }

    @PostMapping("/request/admin/{adminId}")
    public CertificateRequestDTO createCertificateRequestAdmin(@PathVariable Long adminId,
                                                              @RequestBody CertificateRequestDTO certificateRequestDTO) {
        return certificateService.createCertificateRequestForAdmin(adminId, certificateRequestDTO);
    }

    @GetMapping("/all-certificate-requests/{userId}")
    public Collection<CertificateRequestDTO> getAllUsersCertificateRequests(@PathVariable Long userId){
        return certificateService.getAllUsersCertificateRequests(userId);
    }

    @PutMapping("/approve/{requestId}")
    public Certificate approveRequest(@PathVariable Long requestId) {
        return certificateService.createNewCertificate(requestId);
    }

    @PutMapping("/deny/{requestId}")
    public void denyRequest(@PathVariable Long requestId) {

    }

}
