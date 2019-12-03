package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.MediaType;
import org.apache.http.HttpHeaders;

public class FileRequestRecorder implements RequestRecorder {
    private RecorderTape tape;

    public FileRequestRecorder(final RecorderTape tape) {
        this.tape = tape;
    }

    @Override
    public void record(final HttpRequest httpRequest) {
        tape.write(httpRequest);
    }

    @Override
    public MessageContent getContent() {
        HttpRequest request = tape.read();
        return request.getContent();
    }

    @Override
    public MediaType getContentType() {
        HttpRequest httpRequest = tape.read();
        if (httpRequest == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }

        String header = httpRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if (header == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }
        try {
            return MediaType.parse(header);
        } catch (Exception e) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }
    }
}
