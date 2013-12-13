package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
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
            FullHttpRequest httpRequest = new NettyHttpRequestWrapper(request);
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

    private static class NettyHttpRequestWrapper implements FullHttpRequest {

        private HttpRequest request;

        private NettyHttpRequestWrapper(HttpRequest request) {
            this.request = request;
        }

        @Override
        public HttpHeaders trailingHeaders() {
            return null;
        }

        @Override
        public ByteBuf content() {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(request.getContent().getBytes());
            return buffer;
        }

        @Override
        public FullHttpRequest copy() {
            return null;
        }

        @Override
        public HttpContent duplicate() {
            return null;
        }

        @Override
        public FullHttpRequest retain(int increment) {
            return null;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int decrement) {
            return false;
        }

        @Override
        public int refCnt() {
            return 0;
        }

        @Override
        public FullHttpRequest retain() {
            return null;
        }

        @Override
        public HttpVersion getProtocolVersion() {
            return null;
        }

        @Override
        public FullHttpRequest setProtocolVersion(HttpVersion version) {
            return null;
        }

        @Override
        public HttpHeaders headers() {
            DefaultHttpHeaders headers = new DefaultHttpHeaders();
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                headers.add(header.getKey(), header.getValue());
            }
            return headers;
        }

        @Override
        public HttpMethod getMethod() {
            return HttpMethod.valueOf(request.getMethod());
        }

        @Override
        public FullHttpRequest setMethod(HttpMethod method) {
            return null;
        }

        @Override
        public String getUri() {
            return null;
        }

        @Override
        public FullHttpRequest setUri(String uri) {
            return null;
        }

        @Override
        public DecoderResult getDecoderResult() {
            return null;
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
        }
    }
}
