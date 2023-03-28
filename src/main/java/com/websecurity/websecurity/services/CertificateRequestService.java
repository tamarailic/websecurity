package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.repositories.ICertificateRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateGeneratorService certificateGeneratorService;
    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;

    @Override
    public Certificate approveSigningRequest(Long requestId) {
        markRequestAsApproved(requestId);
        return certificateGeneratorService.createNewCertificate(requestId);
    }

    @Override
    public void denySigningRequest(Long requestId) {
        markRequestAsDenied(requestId);
    }

    private void markRequestAsApproved(Long requestId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        request.setStatus("APPROVED");
        certificateRequestRepository.save(request);
    }

    private void markRequestAsDenied(Long requestId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        request.setStatus("DENIED");
        certificateRequestRepository.save(request);
    }
}
