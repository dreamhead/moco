package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;
import org.apache.http.HttpHeaders;

public abstract class AbstractReplayHandler extends AbstractHttpContentResponseHandler implements ReplayHandler {
    protected abstract HttpRequest getRecordedRequest(final HttpRequest request);

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }

        String header = recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE);
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
