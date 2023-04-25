package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.StatusDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class CertificateValidityService implements ICertificateValidityService {

    @Autowired
    private ICertificateRepository certificateRepository;

    @Override
    public StatusDTO checkValidity(String certificateSerialNumber) {
        Certificate certificate = certificateRepository.findById(certificateSerialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist"));
        Boolean isValid = !(isWithdrawn(certificate) || hasExpired(certificate));
        return new StatusDTO(isValid);
    }

    private boolean isWithdrawn(Certificate certificateToCheck) {
        return !certificateToCheck.getValid();
    }

    private boolean hasExpired(Certificate certificate) {
        return certificate.getNotBefore().isAfter(LocalDate.now()) || certificate.getNotAfter().isBefore(LocalDate.now());
    }
}
