package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class FormRequestExtractor implements RequestExtractor<String> {
    private final String key;

    public FormRequestExtractor(String key) {
        this.key = key;
    }

    @Override
    public String extract(HttpRequest request) {
        try {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            InterfaceHttpData data = decoder.getBodyHttpData(key);
            if (data != null && data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute)data;
                return attribute.getValue();
            }

            return null;
        } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException idde) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
