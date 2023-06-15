package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.DownloadCertificateDTO;
import com.websecurity.websecurity.DTO.DownloadPrivateKeyDTO;

import java.security.Principal;

public interface IUploadDownloadCertificateService {
    DownloadCertificateDTO downloadCertificate(String serialNumber);
    DownloadPrivateKeyDTO downloadPrivateKey(String serialNumber, Principal user);
}
