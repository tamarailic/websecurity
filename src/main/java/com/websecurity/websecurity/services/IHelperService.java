package com.websecurity.websecurity.services;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public interface IHelperService {

    PublicKey convertStringToPublicKey(String keyString);

    PublicKey convertBytesToPublicKey(byte[] keyBytes);

    Object getConfigValue(String keyName);

    Date convertLocalDateToDate(LocalDate localDate);

    PrivateKey getPrivateKey(String signingCertificateId);

    KeyPair generateKeyPair();

    String convertKeyToString(PublicKey publicKey);

    byte[] convertKeyToBytes(PublicKey publicKey);

    X509Certificate convertBytesToCertificate(byte[] fileContent);

    LocalDate calculateExpirationDate(LocalDate notBefore, String certificateType);

    BigInteger convertUUIDtoBigInteger(String uuid);
}
