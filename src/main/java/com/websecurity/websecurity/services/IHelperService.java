package com.websecurity.websecurity.services;

import java.security.PublicKey;

public interface IHelperService {

    PublicKey getKey(String keyString);
}
