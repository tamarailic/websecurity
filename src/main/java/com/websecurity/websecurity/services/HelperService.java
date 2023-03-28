package com.websecurity.websecurity.services;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

public class HelperService implements IHelperService {

    @Override
    public PublicKey convertStringToPublicKey(String keyString) {
        try {
            byte[] byteKey = Base64.decode(keyString.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    public Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Object getConfigValue(String keyName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("src/main/resources/config.yaml");
        } catch (IOException e) {
            try {
                inputStream = new FileInputStream("cruise-back/src/main/resources/config.yaml");
            } catch (IOException ex) {
                try {
                    inputStream = new FileInputStream("target/classes/config.yaml");
                } catch (IOException exx) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());
                }
            }
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        return data.get(keyName);
    }
}
