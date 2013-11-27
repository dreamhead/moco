package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class FormRequestExtractor implements RequestExtractor<String> {
    private final String key;

    public FormRequestExtractor(final String key) {
        this.key = key;
    }

    @Override
    public Optional<String> extract(FullHttpRequest request) {
        HttpPostRequestDecoder decoder = null;
        try {
            decoder = new HttpPostRequestDecoder(request);
            return decodeForm(decoder);
        } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException idde) {
            return absent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (decoder != null) {
                decoder.destroy();
            }
        }
    }

    private Optional<String> decodeForm(HttpPostRequestDecoder decoder) throws IOException {
        InterfaceHttpData data = decoder.getBodyHttpData(key);
        if (data != null && data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute)data;
            return of(attribute.getValue());
        }

        return absent();
    }
}
