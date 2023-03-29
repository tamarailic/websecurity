package com.websecurity.websecurity.services;

public interface ICertificateValidityService {

    Boolean checkValidity(String certificateSerialNumber);
}
