package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    private ICertificateRepository certificateRepository;

    public Certificate createNewCertificate() {
        Certificate newCertificate = new Certificate();
        newCertificate.setSerialNumber(1L);
        newCertificate.setVersion(3L);
        certificateRepository.save(newCertificate);
        return newCertificate;
    }

}
