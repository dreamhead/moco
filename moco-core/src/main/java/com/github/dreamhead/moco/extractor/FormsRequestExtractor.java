package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Optional.of;

public final class FormsRequestExtractor extends HttpRequestExtractor<ImmutableMap<String, String>> {
    @Override
    protected Optional<ImmutableMap<String, String>> doExtract(final HttpRequest request) {
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

    private ImmutableMap<String, String> doExtractForms(final HttpPostRequestDecoder decoder) {
        return decoder.getBodyHttpDatas().stream()
                .filter(data -> data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute)
                .map(data -> (Attribute) data)
                .collect(toImmutableMap(Attribute::getName, this::getAttributeValue));
    }

    private String getAttributeValue(final Attribute attribute) {
        try {
            return attribute.getValue();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }
}
