package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.DownloadCertificateDTO;
import com.websecurity.websecurity.DTO.DownloadPrivateKeyDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Objects;

@Service
public class UploadDownloadCertificateService implements IUploadDownloadCertificateService {
    @Autowired
    private ICertificateRepository certificateRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public DownloadCertificateDTO downloadCertificate(String serialNumber) {
        certificateRepository.findById(serialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that serialNumber doesn't exist"));

        try {
            FileInputStream fis = new FileInputStream("src/main/java/com/websecurity/websecurity/security/certs/" + serialNumber + ".crt");
            byte[] certificateContent = Base64.getEncoder().encode(fis.readAllBytes());
            return new DownloadCertificateDTO(certificateContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DownloadPrivateKeyDTO downloadPrivateKey(String serialNumber, Principal user) {
        Certificate certificate = certificateRepository.findById(serialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that serialNumber doesn't exist"));
        User currentUser = userRepository.findUserByUsername(user.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that username doesn't exist"));
        if (!Objects.equals(currentUser.getId(), certificate.getOwner().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User isn't owner of the certificate.");
        try {
            FileInputStream fis = new FileInputStream("src/main/java/com/websecurity/websecurity/security/keys/" + serialNumber + ".key");
            byte[] certificateContent = Base64.getEncoder().encode(fis.readAllBytes());
            return new DownloadPrivateKeyDTO(certificateContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
