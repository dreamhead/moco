package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.util.ToStringHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class AttachmentSetting extends BaseResourceSetting {
    private String filename;

    public String getFilename() {
        return filename;
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("filename", filename);
    }
}
