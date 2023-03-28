package com.websecurity.websecurity.services;

import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Date;

public interface IHelperService {

    PublicKey convertStringToPublicKey(String keyString);

    PublicKey convertBytesToPublicKey(byte[] keyBytes);

    Object getConfigValue(String keyName);

    Date convertLocalDateToDate(LocalDate localDate);
}
