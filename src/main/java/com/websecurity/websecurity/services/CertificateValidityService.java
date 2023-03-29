package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

public class CertificateValidityService implements ICertificateValidityService {

    @Autowired
    private ICertificateRepository certificateRepository;

    @Override
    public Boolean checkValidity(String certificateSerialNumber) {
        Certificate certificate = certificateRepository.findById(certificateSerialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist"));
        if (isWithdrawn(certificate)) return false;
        if (hasExpired(certificate)) return false;
        return true;
    }

    private boolean isWithdrawn(Certificate certificateToCheck) {
        return !certificateToCheck.getValid();
    }

    private boolean hasExpired(Certificate certificate) {
        return certificate.getNotBefore().isAfter(LocalDate.now()) || certificate.getNotAfter().isBefore(LocalDate.now());
    }
}
