package com.websecurity.websecurity.DTO;

public class DownloadPrivateKeyDTO {
    private byte[] keyContent;

    public DownloadPrivateKeyDTO(byte[] keyContent) {
        this.keyContent = keyContent;
    }

    public byte[] getKeyContent() {
        return keyContent;
    }

    public void setKeyContent(byte[] keyContent) {
        this.keyContent = keyContent;
    }
}
