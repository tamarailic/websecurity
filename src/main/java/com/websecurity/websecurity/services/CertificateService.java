package com.websecurity.websecurity.services;

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

import java.util.Optional;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    private ICertificateRepository certificateRepository;

    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;

    @Autowired
    private IUserRepository userRepository;


    public Certificate createNewCertificate() {
        Certificate newCertificate = new Certificate();
        newCertificate.setSerialNumber(1L);
        newCertificate.setVersion(3L);
        certificateRepository.save(newCertificate);
        return newCertificate;
    }

    @Override
    public CertificateRequest createCertificateRequestForUser(Long userId, CertificateRequest certificateRequest) {
        Optional<User> potentialUser = userRepository.findById(userId);
        if(potentialUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist.");
        }

        User user = potentialUser.get();

        if(!certificateRequest.getCertificateType().equals("INTERMEDIATE") | !certificateRequest.getCertificateType().equals("END")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid certificate type.");

        }

        Optional<Certificate> potentialCertificate = certificateRepository.findById(certificateRequest.getIssuerCertificateId());
        if(potentialCertificate.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that ID doesn't exist.");
        }

        Long issuerId = potentialCertificate.get().

        if(userId == issuerId){
            approveCertficate()
        }
        certificateRequestRepository.save(certificateRequest);
        return certificateRequest;


    }
}
