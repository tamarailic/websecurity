package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.DTO.ReasonDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.services.ICertificateRequestService;
import com.websecurity.websecurity.services.ICertificateValidityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.io.Console;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateRequestService certificateRequestService;
    @Autowired
    private ICertificateValidityService certificateValidityService;
    @Autowired
    private ICertificateRepository certificateRepository;


    @PostMapping("/request/user/{userId}")
    public CertificateRequestResponseDTO createCertificateRequestUser(@PathVariable String userId,
                                                                      @RequestBody CertificateRequestDTO certificateRequestDTO) {
        return certificateRequestService.createCertificateRequestForUser(userId, certificateRequestDTO);
    }

    @PostMapping("/request/admin/{adminId}")
    public CertificateRequestResponseDTO createCertificateRequestAdmin(@PathVariable String adminId,
                                                                       @RequestBody CertificateRequestDTO certificateRequestDTO) {
        return certificateRequestService.createCertificateRequestForAdmin(adminId, certificateRequestDTO);
    }

    @GetMapping("/all-certificate-requests/{userId}")
    public Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(@PathVariable String userId) {
        return certificateRequestService.getAllUsersCertificateRequests(userId);
    }

    @PutMapping("/approve/{requestId}")
    public Certificate approveRequest(@PathVariable String requestId) {
        return certificateRequestService.approveSigningRequest(requestId);
    }

    @PutMapping("/deny/{requestId}")
    public void denyRequest(@PathVariable String requestId, @RequestBody ReasonDTO reasonDTO) {
        certificateRequestService.denySigningRequest(requestId, reasonDTO);
    }

    @GetMapping("/verify/{certificateSerialNumber}")
    public Boolean verifyCertificateValidity(@PathVariable String certificateSerialNumber) {
        return certificateValidityService.checkValidity(certificateSerialNumber);
    }

    @PermitAll
    @GetMapping("/all")
    public Page<Certificate> getAllCertificates(Pageable pageable) {
//        System.out.println("ovde");
        return certificateRepository.findAll(pageable);
    }

}
