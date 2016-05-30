package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
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

public class FormsRequestExtractor extends HttpRequestExtractor<ImmutableMap<String, String>> {
    @Override
    protected Optional<ImmutableMap<String, String>> doExtract(final HttpRequest request) {
        HttpPostRequestDecoder decoder = null;
        try {
            decoder = new HttpPostRequestDecoder(((DefaultHttpRequest) request).toFullHttpRequest());
            return of(doExtractForms(decoder));
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException idde) {
            return absent();
        } catch (IOException e) {
            throw new MocoException(e);
        } finally {
            if (decoder != null) {
                decoder.destroy();
            }
        }
    }

    private ImmutableMap<String, String> doExtractForms(final HttpPostRequestDecoder decoder) throws IOException {
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
