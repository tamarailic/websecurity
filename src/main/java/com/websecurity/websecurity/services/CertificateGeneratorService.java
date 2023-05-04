package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.*;
import com.websecurity.websecurity.repositories.ICertificateRepository;
import com.websecurity.websecurity.repositories.ICertificateRequestRepository;
import com.websecurity.websecurity.repositories.IUserRepository;
import jdk.jshell.spi.ExecutionControl;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {
    @Autowired
    private ICertificateRepository certificateRepository;

    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IHelperService helperService;

    @Override
    public Certificate createNewCertificate(String requestId) {
        KeyPair keyPairSubject = helperService.generateKeyPair();
        Certificate newCertificate = createNewCertificateInstance(requestId, keyPairSubject.getPublic());
        SubjectData requesterData = getSubjectData(newCertificate);
        IssuerData issuerData = generateIssuerData(newCertificate, keyPairSubject.getPrivate());
        X509Certificate newCertificateSigned = generateSignedCertificate(requesterData, issuerData);
        saveCertificateToDB(newCertificate);
        saveCertificateToFileSystem(newCertificateSigned, keyPairSubject.getPrivate());
        return newCertificate;
    }

    private Certificate createNewCertificateInstance(String requestId, PublicKey subjectPublicKey) {
        CertificateRequest request = certificateRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with that id does not exist."));
        User requester = userRepository.findById(request.getSubjectId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id does not exist."));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = helperService.calculateExpirationDate(startDate, request.getCertificateType());
        String serialNumber = String.valueOf(helperService.convertUUIDtoBigInteger(UUID.randomUUID().toString()));
        String publicKeyString = helperService.convertKeyToString(subjectPublicKey);
        if (request.getCertificateType().equals("ROOT")) {
            return new Certificate(serialNumber, serialNumber, publicKeyString, new CertificateOwner(requester.getId(), requester.getUsername(), requester.getFirstName(), requester.getLastName()), new CertificateIssuer(requester.getId(), requester.getUsername(), requester.getFirstName(), requester.getLastName()), false, startDate, endDate, (String) helperService.getConfigValue("CERTIFICATE_VERSION"), (String) helperService.getConfigValue("SIGNATURE_ALGORITHM"), true);
        }
        Certificate issuerCertificate = certificateRepository.findById(request.getIssuerCertificateId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate with that id does not exist."));
        issuerCertificate.getHaveSigned().add(serialNumber);
        certificateRepository.save(issuerCertificate);
        User issuer = userRepository.findById(issuerCertificate.getOwner().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id does not exist."));
        if (endDate.isAfter(issuerCertificate.getNotAfter())) {
            endDate = issuerCertificate.getNotAfter();
        }
        return new Certificate(serialNumber, request.getIssuerCertificateId(), publicKeyString, new CertificateOwner(requester.getId(), requester.getUsername(), requester.getFirstName(), requester.getLastName()), new CertificateIssuer(issuer.getId(), issuer.getUsername(), issuer.getFirstName(), issuer.getLastName()), request.getCertificateType().equals("END"), startDate, endDate, (String) helperService.getConfigValue("CERTIFICATE_VERSION"), (String) helperService.getConfigValue("SIGNATURE_ALGORITHM"), true);
    }


    private SubjectData getSubjectData(Certificate certificate) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificate.getOwner().getUsername());
        builder.addRDN(BCStyle.SURNAME, certificate.getOwner().getLastName());
        builder.addRDN(BCStyle.GIVENNAME, certificate.getOwner().getFirstName());
        builder.addRDN(BCStyle.UID, String.valueOf(certificate.getOwner().getId()));

        return new SubjectData(helperService.convertStringToPublicKey(certificate.getPublicKey()), builder.build(), certificate.getSerialNumber(), certificate.getNotBefore(), certificate.getNotAfter());
    }

    private IssuerData generateIssuerData(Certificate certificate, PrivateKey subjectPrivateKey) {
        PrivateKey issuerKey;
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificate.getIssuer().getUsername());
        builder.addRDN(BCStyle.SURNAME, certificate.getIssuer().getLastName());
        builder.addRDN(BCStyle.GIVENNAME, certificate.getIssuer().getFirstName());
        builder.addRDN(BCStyle.UID, String.valueOf(certificate.getIssuer().getId()));
        builder.addRDN(BCStyle.SN, certificate.getSigningCertificateSerialNumber());

        if (Objects.equals(certificate.getSerialNumber(), certificate.getSigningCertificateSerialNumber())) {
            issuerKey = subjectPrivateKey;
        } else {
            issuerKey = helperService.getPrivateKeyForCertificate(certificate.getSigningCertificateSerialNumber());
        }
        return new IssuerData(issuerKey, builder.build());
    }

    private void saveCertificateToDB(Certificate certificateToPersist) {
        certificateRepository.save(certificateToPersist);
    }

    private void saveCertificateToFileSystem(X509Certificate certificateToPersist, PrivateKey privateKeyToPersist) {
        savePublicPartOfCertificate(certificateToPersist);
        savePrivatePartOfCertificate(certificateToPersist, privateKeyToPersist);
    }

    private void savePublicPartOfCertificate(X509Certificate certificateToPersist) {
        try {
            X509CertificateHolder certHolder = new JcaX509CertificateHolder(certificateToPersist);
            FileOutputStream fos = new FileOutputStream("src/main/java/com/websecurity/websecurity/security/certs/" + certificateToPersist.getSerialNumber().toString() + ".crt");
            fos.write(certHolder.getEncoded());
            fos.close();
        } catch (CertificateEncodingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void savePrivatePartOfCertificate(X509Certificate certificateToPersist, PrivateKey privateKeyToPersist) {
        try {
            JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter("src/main/java/com/websecurity/websecurity/security/keys/" + certificateToPersist.getSerialNumber().toString() + ".key"));
            pemWriter.writeObject(privateKeyToPersist);
            pemWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public X509Certificate generateSignedCertificate(SubjectData subjectData, IssuerData issuerData) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder((String) helperService.getConfigValue("SIGNATURE_ALGORITHM"));
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
            Object certificateVersion = helperService.getConfigValue("CERTIFICATE_VERSION");

            if (!certificateVersion.equals("v3")) {
                throw new ExecutionControl.NotImplementedException("Only " + certificateVersion + " is implemented!");
            }

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    helperService.convertLocalDateToDate(subjectData.getStartDate()),
                    helperService.convertLocalDateToDate(subjectData.getEndDate()),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException |
                 CertificateException e) {
            e.printStackTrace();
        } catch (ExecutionControl.NotImplementedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
