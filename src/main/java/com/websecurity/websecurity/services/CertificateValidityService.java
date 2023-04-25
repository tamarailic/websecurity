package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.StatusDTO;
import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;

@Service
public class CertificateValidityService implements ICertificateValidityService {

    @Autowired
    private IHelperService helperService;
    @Autowired
    private ICertificateRepository certificateRepository;

    @Override
    public StatusDTO checkValidity(String certificateSerialNumber) {
        Certificate certificate = certificateRepository.findById(certificateSerialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist"));
        Boolean isValid = !(isWithdrawn(certificate) || hasExpired(certificate));
        return new StatusDTO(isValid);
    }

    @Override
    public StatusDTO checkFileValidity(byte[] fileContent) {
        X509Certificate certificateFile = helperService.convertBytesToCertificate(fileContent);
        Certificate certificate = certificateRepository.findById(String.valueOf(certificateFile.getSerialNumber())).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist"));
        Boolean isValid = !(isWithdrawn(certificate) || hasExpired(certificate) || hasBeenCorrupted(certificateFile));
        return new StatusDTO(isValid);
    }

    private boolean isWithdrawn(Certificate certificateToCheck) {
        return !certificateToCheck.getValid();
    }

    private boolean hasExpired(Certificate certificate) {
        return certificate.getNotBefore().isAfter(LocalDate.now()) || certificate.getNotAfter().isBefore(LocalDate.now());
    }

    private boolean hasBeenCorrupted(X509Certificate certificate) {
        String issuerCertificateSerialNumber = hexStringToByteArray(certificate.getIssuerX500Principal().getName().split(",")[0].split("#")[1]);

        Certificate issuerCertificate = certificateRepository.findById(issuerCertificateSerialNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist"));
        PublicKey issuerPublicKey = helperService.convertStringToPublicKey(issuerCertificate.getPublicKey());
        try {
            certificate.verify(issuerPublicKey);
            return false;
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            return true;
        }
    }

    public String hexStringToByteArray(String hex) {
        StringBuilder result = new StringBuilder();
        char[] charArray = hex.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            char ch = (char) Integer.parseInt(st, 16);
            result.append(ch);
        }
        return result.toString().split("'")[1];
    }
}
