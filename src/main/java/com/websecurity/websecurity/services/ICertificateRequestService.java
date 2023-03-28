package com.websecurity.websecurity.services;

import com.websecurity.websecurity.models.Certificate;

public interface ICertificateRequestService {

    Certificate approveSigningRequest(Long requestId);

    void denySigningRequest(Long requestId);

}
