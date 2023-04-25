package com.websecurity.websecurity.services;

import com.websecurity.websecurity.DTO.DownloadCertificateDTO;

public interface IUploadDownloadCertificateService {
    DownloadCertificateDTO download(String serialNumber);
}
