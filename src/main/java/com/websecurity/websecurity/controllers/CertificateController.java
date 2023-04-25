package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.*;
import com.websecurity.websecurity.services.ICertificateRequestService;
import com.websecurity.websecurity.services.ICertificateValidityService;
import com.websecurity.websecurity.services.IUploadDownloadCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.Collection;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateRequestService certificateRequestService;
    @Autowired
    private ICertificateValidityService certificateValidityService;

    @Autowired
    private IUploadDownloadCertificateService uploadDownloadCertificateService;

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

    @GetMapping("/all-requests-to-review/{userId}")
    public Collection<CertificateRequestResponseDTO> getAllRequestsToReview(@PathVariable String userId) {
        return certificateRequestService.getAllUsersCertificateRequestsToReview(userId);
    }

    @GetMapping("/all-certificate-requests")
    public Collection<CertificateRequestResponseDTO> getAllCertificateRequests() {
        return certificateRequestService.getAllCertificateRequests();
    }

    @PutMapping("/approve/{requestId}")
    public CertificateToShowDTO approveRequest(@PathVariable String requestId) {
        return certificateRequestService.approveSigningRequest(requestId);
    }

    @PutMapping("/deny/{requestId}")
    public void denyRequest(@PathVariable String requestId, @RequestBody ReasonDTO reasonDTO) {
        certificateRequestService.denySigningRequest(requestId, reasonDTO);
    }

    @GetMapping("/verify/{certificateSerialNumber}")
    public StatusDTO verifyCertificateValidityFromId(@PathVariable String certificateSerialNumber) {
        return certificateValidityService.checkValidity(certificateSerialNumber);
    }
    @PostMapping(value = "/verify/file", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public StatusDTO verifyCertificateValidityFromFile(@RequestBody byte[] certificateContent) {
        return certificateValidityService.checkFileValidity(certificateContent);
    }

    @PermitAll
    @GetMapping("/all")
    public Page<CertificateToShowDTO> getAllCertificates(Pageable pageable) {
        return certificateRequestService.getAll(pageable);
    }

    @GetMapping("/download-certificate/{certificateSerialNumber}")
    public DownloadCertificateDTO downloadCertificate(@PathVariable String certificateSerialNumber) {
        return uploadDownloadCertificateService.download(certificateSerialNumber);
    }

    @PutMapping("/withdraw/{certificateSerialNumber}")
    public CertificateToShowDTO withdrawCertificate(@PathVariable String certificateSerialNumber, @RequestBody ReasonDTO reason){
        return certificateRequestService.withdrawCertificateById(certificateSerialNumber, reason);
    }
}
