package com.websecurity.websecurity.DTO;

public class DownloadCertificateDTO {

    private byte[] certificateContent;

    public DownloadCertificateDTO(byte[] certificateContent) {
        this.certificateContent = certificateContent;
    }

    public byte[] getCertificateContent() {
        return certificateContent;
    }

    public void setCertificateContent(byte[] certificateContent) {
        this.certificateContent = certificateContent;
    }
}
