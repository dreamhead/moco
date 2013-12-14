package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newHashMap;

public class FormsRequestExtractor implements RequestExtractor {
    public Optional<ImmutableMap<String, String>> extract(HttpRequest request) {

        HttpPostRequestDecoder decoder = null;
        try {
            FullHttpRequest httpRequest = wrapRequest(request);
            decoder = new HttpPostRequestDecoder(httpRequest);
            return of(doExtractForms(decoder));
        } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException idde) {
            return absent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (decoder != null) {
                decoder.destroy();
            }
        }
    }

    private FullHttpRequest wrapRequest(HttpRequest request) {
        ByteBuf buffer = Unpooled.buffer();
        if (request.getContent() != null) {
            buffer.writeBytes(request.getContent().getBytes());
        }
        return new DefaultFullHttpRequest(HttpVersion.valueOf(request.getVersion()),
                HttpMethod.valueOf(request.getMethod()), request.getUri(), buffer);
    }

    private ImmutableMap<String, String> doExtractForms(HttpPostRequestDecoder decoder) throws IOException {
        List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
        Map<String, String> forms = newHashMap();
        for (InterfaceHttpData data : bodyHttpDatas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) data;
                forms.put(attribute.getName(), attribute.getValue());
            }
        }

        return copyOf(forms);
    }
}
