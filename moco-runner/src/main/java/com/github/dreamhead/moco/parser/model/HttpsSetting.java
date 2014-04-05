package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpsSetting {
    private String certificate;

    @JsonProperty("key_store_password")
    private String keyStorePassword;

    @JsonProperty("cert_password")
    private String certPassword;

    public String getCertificate() {
        return certificate;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getCertPassword() {
        return certPassword;
    }
}
