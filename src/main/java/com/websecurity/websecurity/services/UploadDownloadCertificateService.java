package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.DownloadCertificateDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

@Service
public class UploadDownloadCertificateService implements IUploadDownloadCertificateService{
    @Autowired
    private ICertificateRepository certificateRepository;

    @Override
    public DownloadCertificateDTO download(String serialNumber) {
        Certificate certificate = certificateRepository.findById(serialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate with that serialNumber doesn't exist"));
        String certificateFileName = (new BigInteger(certificate.getSerialNumber().replace("-", ""), 16)).toString();

        try {
            FileInputStream fis = new FileInputStream("src/main/java/com/websecurity/websecurity/security/certs/" + certificateFileName + ".crt");
            byte[] certificateContent = Base64.encode(fis.readAllBytes());
            return new DownloadCertificateDTO(certificateContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
