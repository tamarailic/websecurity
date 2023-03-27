package com.websecurity.websecurity.services;

import java.security.PublicKey;

public interface IHelperService {

    PublicKey convertStringToPublicKey(String keyString);

    PublicKey convertBytesToPublicKey(byte[] keyBytes);

    Object getConfigValue(String keyName);
}
