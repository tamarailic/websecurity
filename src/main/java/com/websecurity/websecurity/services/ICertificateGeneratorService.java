package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;

public interface ICertificateGeneratorService {
    Certificate createNewCertificate(Long requestId);
}
