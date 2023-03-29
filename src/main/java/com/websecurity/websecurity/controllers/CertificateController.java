package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.services.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateRequestService certificateRequestService;


    @PostMapping("/request/user/{userId}")
    public CertificateRequestDTO createCertificateRequestUser(@PathVariable Long userId,
                                                              @RequestBody CertificateRequestDTO certificateRequestDTO) {
        return certificateRequestService.createCertificateRequestForUser(userId, certificateRequestDTO);
    }

    @PostMapping("/request/admin/{adminId}")
    public CertificateRequestDTO createCertificateRequestAdmin(@PathVariable Long adminId,
                                                               @RequestBody CertificateRequestDTO certificateRequestDTO) {
        return certificateRequestService.createCertificateRequestForAdmin(adminId, certificateRequestDTO);
    }

    @GetMapping("/all-certificate-requests/{userId}")
    public Collection<CertificateRequestDTO> getAllUsersCertificateRequests(@PathVariable Long userId) {
        return certificateRequestService.getAllUsersCertificateRequests(userId);
    }

    @PutMapping("/approve/{requestId}")
    public Certificate approveRequest(@PathVariable Long requestId) {
        return certificateRequestService.approveSigningRequest(requestId);
    }

    @PutMapping("/deny/{requestId}")
    public void denyRequest(@PathVariable Long requestId) {
        certificateRequestService.denySigningRequest(requestId);
    }

}
