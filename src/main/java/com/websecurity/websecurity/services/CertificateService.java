package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;
<<<<<<< HEAD
import com.websecurity.websecurity.models.CertificateRequest;
import com.websecurity.websecurity.models.User;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.repositories.ICertificateRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
=======
import com.websecurity.websecurity.models.SubjectData;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
>>>>>>> development
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import java.security.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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
        SubjectData requester = getSubjectData(requestId);
    }

    private SubjectData getSubjectData(Long requestId) {
        try {

            Optional<CertificateRequest> request = certificateRequestRepository.findById(requestId);

            PublicKey subjectPublicKey = helperService.getKey();

            KeyPair keyPairSubject = generateKeyPair();

            // Datumi od kad do kad vazi sertifikat
            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = iso8601Formater.parse("2022-03-01");
            Date endDate = iso8601Formater.parse("2024-03-01");

            // Serijski broj sertifikata
            String sn = "1";

            // klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, "Marija Kovacevic");
            builder.addRDN(BCStyle.SURNAME, "Kovacevic");
            builder.addRDN(BCStyle.GIVENNAME, "Marija");
            builder.addRDN(BCStyle.O, "UNS-FTN");
            builder.addRDN(BCStyle.OU, "Katedra za informatiku");
            builder.addRDN(BCStyle.C, "RS");
            builder.addRDN(BCStyle.E, "marija.kovacevic@uns.ac.rs");

            // UID (USER ID) je ID korisnika
            builder.addRDN(BCStyle.UID, "654321");

            // Kreiraju se podaci za sertifikat, sto ukljucuje:
            // - javni kljuc koji se vezuje za sertifikat
            // - podatke o vlasniku
            // - serijski broj sertifikata
            // - od kada do kada vazi sertifikat
            return new SubjectData(keyPairSubject.getPublic(), builder.build(), sn, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

        Long issuerId = potentialCertificate.get().getIssuer().getIssuerId();

        if(userId == issuerId){
            approveCertficate()
        }
        certificateRequestRepository.save(certificateRequest);
        return certificateRequest;
        
    }
}
