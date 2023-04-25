package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.CertificateRequestDTO;
import com.websecurity.websecurity.DTO.CertificateRequestResponseDTO;
import com.websecurity.websecurity.DTO.CertificateToShowDTO;
import com.websecurity.websecurity.DTO.ReasonDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.repositories.ICertificateRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateGeneratorService certificateGeneratorService;
    @Autowired
    private IHelperService helperService;
    @Autowired
    private ICertificateValidityService certificateValidityService;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid certificate type requested.");
        }

        Certificate certificate = certificateRepository.findById(certificateRequestDTO.getIssuerCertificateId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist."));

        validateCertificateRequest(certificateRequestDTO.getCertificateType(), certificate);

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
            Certificate certificate = certificateRepository.findById(certificateRequestDTO.getIssuerCertificateId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist."));
            validateCertificateRequest(certificateRequestDTO.getCertificateType(), certificate);
        }

        CertificateRequest certificateRequest = new CertificateRequest(certificateRequestDTO, adminId, LocalDateTime.now(), "PENDING");
        certificateRequestRepository.save(certificateRequest);

        approveSigningRequest(certificateRequest.getId());
        CertificateRequest approvedCertificateRequest = certificateRequestRepository.findById(certificateRequest.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request with that ID doesn't exist."));

        return new CertificateRequestResponseDTO(approvedCertificateRequest);
    }


    private void validateCertificateRequest(String requestedCertificateType, Certificate certificate) {
        if (!certificateValidityService.checkValidity(certificate.getSerialNumber()).getStatus()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Issuer certificate isn't valid.");
        }

        if (!canGenerateCertificate(certificate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Issuer certificate doesn't have permission to generate new certificate.");
        }
    }

    private boolean canGenerateCertificate(Certificate certificate) {
        return !certificate.isEndCertificate();
    }

    @Override
    public Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequests(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist."));
        return certificateRequestRepository.findAllBySubjectId(userId).stream().map(CertificateRequestResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public Collection<CertificateRequestResponseDTO> getAllCertificateRequests() {
        return certificateRequestRepository.findAll().stream().map(CertificateRequestResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public Collection<CertificateRequestResponseDTO> getAllUsersCertificateRequestsToReview(String userId) {
        Set<Certificate> userCertificates = certificateRepository.findAllByOwnerId(userId);
        Set<CertificateRequest> certificateRequestsToReview = new HashSet<>();
        for (Certificate certificate : userCertificates) {
            certificateRequestsToReview.addAll(certificateRequestRepository.findAllByIssuerCertificateId(certificate.getSerialNumber()));
        }

        return certificateRequestsToReview.stream().filter(certificateRequest -> certificateRequest.getStatus().equals("PENDING")).map(CertificateRequestResponseDTO::new).collect(Collectors.toSet());
    }

    @Override
    public CertificateToShowDTO approveSigningRequest(String requestId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        if (!request.getStatus().equals("PENDING"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request was already processed");
        markRequestAsApproved(request);
        return new CertificateToShowDTO(certificateGeneratorService.createNewCertificate(requestId));
    }

    @Override
    public void denySigningRequest(String requestId, ReasonDTO denyReason) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        if (!request.getStatus().equals("PENDING"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate request was already processed");
        markRequestAsDenied(request, denyReason.getReason());
    }

    @Override
    public Page<CertificateToShowDTO> getAll(Pageable pageable) {
        return certificateRepository.findAll(pageable).map(CertificateToShowDTO::new);
    }

    @Override
    public CertificateToShowDTO withdrawCertificateById(String certificateSerialNumber, ReasonDTO reason) {
        Certificate certificateToWithdraw = certificateRepository.findById(certificateSerialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist."));
        if (!certificateToWithdraw.getValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate already withdrawn");
        }
        certificateToWithdraw.setValid(false);
        certificateToWithdraw.setWithdrawReason(reason.getReason());
        certificateRepository.save(certificateToWithdraw);
        for (String certificateIdThatWasSignedByWithdrawnCertificate : certificateToWithdraw.getHaveSigned()) {
            _withdrawCertificateById(certificateIdThatWasSignedByWithdrawnCertificate, reason);
        }
        return new CertificateToShowDTO(certificateToWithdraw);
    }

    private void _withdrawCertificateById(String certificateSerialNumber, ReasonDTO reason) {
        Certificate certificateToWithdraw = certificateRepository.findById(certificateSerialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist."));
        if (!certificateToWithdraw.getValid()) return;
        certificateToWithdraw.setValid(false);
        certificateToWithdraw.setWithdrawReason(reason.getReason());
        certificateRepository.save(certificateToWithdraw);
        for (String certificateIdThatWasSignedByWithdrawnCertificate : certificateToWithdraw.getHaveSigned()) {
            withdrawCertificateById(certificateIdThatWasSignedByWithdrawnCertificate, reason);
        }
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
