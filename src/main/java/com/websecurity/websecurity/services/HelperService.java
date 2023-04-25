package com.websecurity.websecurity.services;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
public class HelperService implements IHelperService {

    @Override
    public PublicKey convertStringToPublicKey(String keyString) {
        byte[] byteKey = Base64.decode(keyString.getBytes());
        return convertBytesToPublicKey(byteKey);
    }

    @Override
    public String convertKeyToString(PublicKey publicKey) {
        return Base64.toBase64String(convertKeyToBytes(publicKey));
    }

    @Override
    public byte[] convertKeyToBytes(PublicKey publicKey) {
        return publicKey.getEncoded();
    }

    @Override
    public X509Certificate convertBytesToCertificate(byte[] fileContent) {
        X509Certificate certificate;
        try {
            X509CertificateHolder certificateHolder = new X509CertificateHolder(fileContent);
            JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
            converter.setProvider(new BouncyCastleProvider());
            certificate = converter.getCertificate(certificateHolder);
        } catch (CertificateException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid certificate");
        }
        return certificate;
    }

    @Override
    public PublicKey convertBytesToPublicKey(byte[] keyBytes) {
        try {
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Object getConfigValue(String keyName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("src/main/resources/config.yaml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        return data.get(keyName);
    }

    @Override
    public PrivateKey getPrivateKeyForCertificate(String signingCertificateId) {
        try {
            BigInteger signingCertificateName = new BigInteger(signingCertificateId);
            // Read the private key from the file
            File keyFile = new File("src/main/java/com/websecurity/websecurity/security/keys/" + signingCertificateName + ".key");
            PEMParser pemParser = new PEMParser(new FileReader(keyFile));
            Object obj = pemParser.readObject();
            pemParser.close();

            // Convert the parsed object to a private key
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            PrivateKey privateKey = null;
            if (obj instanceof PEMKeyPair) {
                privateKey = converter.getPrivateKey(((PEMKeyPair) obj).getPrivateKeyInfo());
            } else if (obj instanceof PrivateKeyInfo) {
                privateKey = converter.getPrivateKey((PrivateKeyInfo) obj);
            }
            return privateKey;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KeyPair generateKeyPair() {
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
    public LocalDate calculateExpirationDate(LocalDate notBefore, String certificateType) {
        LocalDate expirationDate;
        switch (certificateType) {
            case "END":
                expirationDate = notBefore.plusDays((Integer) getConfigValue("END_CERTIFICATE_DURATION_IN_DAYS"));
                break;
            case "INTERMEDIATE":
                expirationDate = notBefore.plusDays((Integer) getConfigValue("INTERMEDIATE_CERTIFICATE_DURATION_IN_DAYS"));
                break;
            case "ROOT":
                expirationDate = notBefore.plusDays((Integer) getConfigValue("ROOT_CERTIFICATE_DURATION_IN_DAYS"));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Certificate type does not exist");
        }
        return expirationDate;
    }

    @Override
    public BigInteger convertUUIDtoBigInteger(String uuid) {
        return new BigInteger(uuid.replace("-", ""), 16);
    }
}
