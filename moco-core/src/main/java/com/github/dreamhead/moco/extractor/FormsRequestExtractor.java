package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

import static com.github.dreamhead.moco.util.Maps.toOrderedMap;
import static java.util.Optional.of;

public final class FormsRequestExtractor extends HttpRequestExtractor<Map<String, String>> {
    @Override
    protected Optional<Map<String, String>> doExtract(final HttpRequest request) {
        HttpPostRequestDecoder decoder = null;
        try {
            FullHttpRequest targetRequest = ((DefaultHttpRequest) request).toFullHttpRequest();
            Charset charset = HttpUtil.getCharset(targetRequest);
            HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE, charset);
            decoder = new HttpPostRequestDecoder(factory, targetRequest, charset);
            return of(doExtractForms(decoder));
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException idde) {
            return Optional.empty();
        } finally {
            if (decoder != null) {
                decoder.destroy();
            }
        }
    }

    private Map<String, String> doExtractForms(final HttpPostRequestDecoder decoder) {
        return decoder.getBodyHttpDatas().stream()
                .filter(data -> data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute)
                .map(data -> (Attribute) data)
                .collect(toOrderedMap(Attribute::getName, this::getAttributeValue));
    }

    private String getAttributeValue(final Attribute attribute) {
        try {
            return attribute.getValue();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
