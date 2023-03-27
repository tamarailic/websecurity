package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;
import com.websecurity.websecurity.models.CertificateRequest;

public interface ICertificateService {

    Certificate createNewCertificate();

    CertificateRequest createCertificateRequest(Long userId, CertificateRequest certificateRequest);

}
