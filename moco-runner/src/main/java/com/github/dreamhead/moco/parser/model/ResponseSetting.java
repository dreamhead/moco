package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.ResponseHandlerFactory;
import com.google.common.base.MoreObjects;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseSetting extends BaseResourceSetting {
    private static final ResponseHandlerFactory factory = new DynamicResponseHandlerFactory();

    private String status;
    private ProxyContainer proxy;
    private Map<String, TextContainer> headers;
    private Map<String, CookieContainer> cookies;
    private LatencyContainer latency;

    private TextContainer version;
    private AttachmentSetting attachment;
    private CollectionContainer seq;
    private CollectionContainer cycle;
    private ReplayContainer record;
    private ReplayContainer replay;

    private CorsContainer cors;

    protected final ResponseSetting asResponseSetting() {
        ResponseSetting responseSetting = asBaseResourceSetting(new ResponseSetting());
        responseSetting.status = status;
        responseSetting.proxy = proxy;
        responseSetting.headers = headers;
        responseSetting.cookies = cookies;
        responseSetting.latency = latency;
        responseSetting.version = version;
        responseSetting.attachment = attachment;
        responseSetting.seq = seq;
        responseSetting.cycle = cycle;
        responseSetting.record = record;
        responseSetting.replay = replay;
        responseSetting.cors = cors;

        return responseSetting;
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("version", version)
                .add("status", status)
                .add("headers", headers)
                .add("cookies", cookies)
                .add("proxy", proxy)
                .add("latency", latency)
                .add("attachment", attachment)
                .add("seq", seq)
                .add("cycle", cycle)
                .add("record", record)
                .add("replay", replay)
                .add("cors", cors);

    }

    public ResponseHandler getResponseHandler() {
        return factory.createResponseHandler(this);
    }
}
