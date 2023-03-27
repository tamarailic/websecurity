package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.repositories.ICertificateRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import com.websecurity.websecurity.models.SubjectData;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import java.security.*;
import java.text.ParseException;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    private ICertificateRepository certificateRepository;

    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IHelperService helperService;

    public Certificate createNewCertificate(Long requestId) {
        SubjectData requesterData = getSubjectData(requestId);
        //TODO: zavrsiti implementaciju kreiranja
        return null;
    }

    private SubjectData getSubjectData(Long requestId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        User requester = userRepository.findById(request.getSubjectId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id does not exist."));
        PublicKey subjectPublicKey = helperService.convertBytesToPublicKey(requester.getPublicKey().getEncoded());

        // Datumi od kad do kad vazi sertifikat
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateExpirationDate(startDate, request.getCertificateType());

        // Serijski broj sertifikata
        String sn = "1";

        // klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, requester.getUsername());
        builder.addRDN(BCStyle.SURNAME, requester.getLastName());
        builder.addRDN(BCStyle.GIVENNAME, requester.getFirstName());
        builder.addRDN(BCStyle.O, "websecurity");
        builder.addRDN(BCStyle.OU, "websecurity");
        builder.addRDN(BCStyle.C, "RS");
        builder.addRDN(BCStyle.E, requester.getUsername());

        // UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, String.valueOf(requester.getId()));

        // Kreiraju se podaci za sertifikat, sto ukljucuje:
        // - javni kljuc koji se vezuje za sertifikat
        // - podatke o vlasniku
        // - serijski broj sertifikata
        // - od kada do kada vazi sertifikat
        return new SubjectData(subjectPublicKey, builder.build(), sn, startDate, endDate);
    }

    private Boolean saveCertificateToDataBase() {
        // TODO: Implementirati cuvanje u bazu
        return true;
    }

    private Boolean saveCertificateToFileSystem() {
        // TODO: Implementirati cuvanje u fajlsistemu
        return true;
    }


    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LocalDate calculateExpirationDate(LocalDate notBefore, String certificateType) {
        LocalDate expirationDate;
        switch (certificateType) {
            case "END":
                expirationDate = notBefore.plusDays((Long) helperService.getConfigValue("END_CERTIFICATE_DURATION_IN_DAYS"));
                break;
            case "INTERMEDIATE":
                expirationDate = notBefore.plusDays((Long) helperService.getConfigValue("INTERMEDIATE_CERTIFICATE_DURATION_IN_DAYS"));
                break;
            case "ROOT":
                expirationDate = notBefore.plusDays((Long) helperService.getConfigValue("ROOT_CERTIFICATE_DURATION_IN_DAYS"));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate type does not exist");
        }
        return expirationDate;
    }

    @Override
    public CertificateRequest createCertificateRequest(Long userId, CertificateRequest certificateRequest) {
        return null;
    }
}
