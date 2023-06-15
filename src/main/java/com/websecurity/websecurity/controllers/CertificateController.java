package com.websecurity.websecurity.controllers;

import com.websecurity.websecurity.DTO.*;
import com.websecurity.websecurity.logging.WSLogger;
import com.websecurity.websecurity.services.ICertificateRequestService;
import com.websecurity.websecurity.services.ICertificateValidityService;
import com.websecurity.websecurity.services.IUploadDownloadCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateRequestService certificateRequestService;
    @Autowired
    private ICertificateValidityService certificateValidityService;
    @Autowired
    private IUploadDownloadCertificateService uploadDownloadCertificateService;

    @PostMapping("/request")
    @WSLogger
    public CertificateRequestResponseDTO createCertificateRequest(@RequestBody CertificateRequestDTO certificateRequestDTO, Authentication token) {
        UserDetails userDetails = (UserDetails) token.getPrincipal();
        String role = userDetails.getAuthorities().stream().collect(Collectors.toList()).get(0).getAuthority();
        if (role.equals("admin")) {
            return certificateRequestService.createCertificateRequestForAdmin(userDetails.getUsername(), certificateRequestDTO);
        } else {
            return certificateRequestService.createCertificateRequestForUser(userDetails.getUsername(), certificateRequestDTO);
        }
    }

    @GetMapping("/all-certificate-requests")
    public Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(Authentication token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token missing");
        }
        UserDetails userDetails = (UserDetails) token.getPrincipal();
        String role = userDetails.getAuthorities().stream().collect(Collectors.toList()).get(0).getAuthority();
        if (role.equals("admin")) {
            return certificateRequestService.getAllCertificateRequests();
        } else {
            return certificateRequestService.getAllUsersCertificateRequests(userDetails.getUsername());
        }
    }

    @GetMapping("/all-requests-to-review")
    public Collection<CertificateRequestResponseDTO> getAllRequestsToReview(Principal user) {
        return certificateRequestService.getAllUsersCertificateRequestsToReview(user);
    }

    @PutMapping("/approve/{requestId}")
    @WSLogger
    public CertificateToShowDTO approveRequestForCertificate(@PathVariable String requestId) {
        return certificateRequestService.approveSigningRequest(requestId);
    }

    @PutMapping("/deny/{requestId}")
    @WSLogger
    public void denyRequestForCertificate(@PathVariable String requestId, @RequestBody ReasonDTO reasonDTO) {
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
    @WSLogger
    public Page<CertificateToShowDTO> getAllCertificates(Pageable pageable) {
        return certificateRequestService.getAll(pageable);
    }

    @GetMapping("/download-certificate/{certificateSerialNumber}")
    @WSLogger
    public DownloadCertificateDTO downloadCertificate(@PathVariable String certificateSerialNumber) {
        return uploadDownloadCertificateService.downloadCertificate(certificateSerialNumber);
    }

    @GetMapping("/download-privateKey/{certificateSerialNumber}")
    @WSLogger
    public DownloadPrivateKeyDTO downloadPrivateKey(@PathVariable String certificateSerialNumber, Principal user) {
        return uploadDownloadCertificateService.downloadPrivateKey(certificateSerialNumber, user);
    }

    @PutMapping("/withdraw/{certificateSerialNumber}")
    @WSLogger
    public CertificateToShowDTO withdrawCertificate(@PathVariable String certificateSerialNumber, @RequestBody ReasonDTO reason) {
        return certificateRequestService.withdrawCertificateById(certificateSerialNumber, reason);
    }
}
