package com.websecurity.websecurity.services;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Date;

public interface IHelperService {

    PublicKey convertStringToPublicKey(String keyString);

    PublicKey convertBytesToPublicKey(byte[] keyBytes);

    Object getConfigValue(String keyName);

    Date convertLocalDateToDate(LocalDate localDate);

    PrivateKey getPrivateKey(String signingCertificateId);

    KeyPair generateKeyPair();

    String convertKeyToString(PublicKey publicKey);

    byte[] convertKeyToBytes(PublicKey publicKey);

    LocalDate calculateExpirationDate(LocalDate notBefore, String certificateType);

    String getEmailFrom();

    int getVerificationExpiration();

    String getTwilioPhone();

    String getTwilioToken();

    String getTwilioSID();
}
