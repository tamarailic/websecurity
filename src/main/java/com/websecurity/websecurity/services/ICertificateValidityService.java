package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.StatusDTO;

public interface ICertificateValidityService {

    StatusDTO checkValidity(String certificateSerialNumber);

    StatusDTO checkFileValidity(byte[] fileContent);
}
