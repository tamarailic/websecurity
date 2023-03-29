package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

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
    public CertificateRequestDTO createCertificateRequestForUser(Long userId, CertificateRequestDTO certificateRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));

        if (!certificateRequestDTO.getCertificateType().equals("INTERMEDIATE") & !certificateRequestDTO.getCertificateType().equals("END")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid certificate type.");
        }

        Certificate certificate = certificateRepository.findById(certificateRequestDTO.getIssuerCertificateId()).orElseThrow(() ->  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist."));

        certificateRequestDTO.setStatus("PENDING");
        CertificateRequest certificateRequest = new CertificateRequest(certificateRequestDTO);
        certificateRequestRepository.save(certificateRequest);

        Long issuerId = certificate.getIssuer().getId();

        if (Objects.equals(user.getId(), issuerId)) {
            approveSigningRequest(certificateRequest.getId());
        }

        CertificateRequest approvedCertificateRequest = certificateRequestRepository.findById(certificateRequest.getId()).orElseThrow(() ->  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request with that ID doesn't exist."));

        return new CertificateRequestDTO(approvedCertificateRequest);
    }

    @Override
    public CertificateRequestDTO createCertificateRequestForAdmin(Long adminId, CertificateRequestDTO certificateRequestDTO) {
        userRepository.findById(adminId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));
        certificateRepository.findById(certificateRequestDTO.getIssuerCertificateId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist."));

        certificateRequestDTO.setStatus("PENDING");
        CertificateRequest certificateRequest = new CertificateRequest(certificateRequestDTO);
        certificateRequestRepository.save(certificateRequest);

        approveSigningRequest(certificateRequest.getId());
        CertificateRequest approvedCertificateRequest = certificateRequestRepository.findById(certificateRequest.getId()).orElseThrow(() ->  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request with that ID doesn't exist."));

        return new CertificateRequestDTO(approvedCertificateRequest);
    }

    @Override
    public Collection<CertificateRequestDTO> getAllUsersCertificateRequests(Long userId) {
       userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));

        return certificateRequestRepository.findAllBySubjectId(userId);
    }
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
