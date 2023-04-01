package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.DTO.ReasonDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.repositories.ICertificateRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateGeneratorService certificateGeneratorService;
    @Autowired
    private ICertificateRepository certificateRepository;
    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;

    @Autowired
    private IUserRepository userRepository;


    @Override
    public CertificateRequestResponseDTO createCertificateRequestForUser(String userId, CertificateRequestDTO certificateRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));

        if (!certificateRequestDTO.getCertificateType().equals("INTERMEDIATE") & !certificateRequestDTO.getCertificateType().equals("END")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid certificate type.");
        }

        Certificate certificate = certificateRepository.findById(certificateRequestDTO.getIssuerCertificateId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist."));

        CertificateRequest certificateRequest = new CertificateRequest(certificateRequestDTO, userId, LocalDateTime.now(), "PENDING");
        certificateRequestRepository.save(certificateRequest);

        String ownerId = certificate.getOwner().getId();

        if (Objects.equals(user.getId(), ownerId)) {
            approveSigningRequest(certificateRequest.getId());
        }

        CertificateRequest approvedCertificateRequest = certificateRequestRepository.findById(certificateRequest.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request with that ID doesn't exist."));

        return new CertificateRequestResponseDTO(approvedCertificateRequest);
    }

    @Override
    public CertificateRequestResponseDTO createCertificateRequestForAdmin(String adminId, CertificateRequestDTO certificateRequestDTO) {
        userRepository.findById(adminId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));
        if (!certificateRequestDTO.getCertificateType().equals("ROOT")) {
            certificateRepository.findById(certificateRequestDTO.getIssuerCertificateId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist."));
        }

        CertificateRequest certificateRequest = new CertificateRequest(certificateRequestDTO, adminId, LocalDateTime.now(), "PENDING");
        certificateRequestRepository.save(certificateRequest);

        approveSigningRequest(certificateRequest.getId());
        CertificateRequest approvedCertificateRequest = certificateRequestRepository.findById(certificateRequest.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request with that ID doesn't exist."));

        return new CertificateRequestResponseDTO(approvedCertificateRequest);
    }

    @Override
    public Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));
        return certificateRequestRepository.findAllBySubjectId(userId);
    }

    @Override
    public Certificate approveSigningRequest(String requestId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        if (!request.getStatus().equals("PENDING"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request was already processed");
        markRequestAsApproved(request);
        return certificateGeneratorService.createNewCertificate(requestId);
    }

    @Override
    public void denySigningRequest(String requestId, ReasonDTO denyReason) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        if (!request.getStatus().equals("PENDING"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request was already processed");
        markRequestAsDenied(request, denyReason.getReason());
    }

    private void markRequestAsApproved(CertificateRequest request) {
        request.setStatus("APPROVED");
        certificateRequestRepository.save(request);
    }

    private void markRequestAsDenied(CertificateRequest request, String denyReason) {
        request.setStatus("DENIED");
        request.setDenyReason(denyReason);
        certificateRequestRepository.save(request);
    }
}
