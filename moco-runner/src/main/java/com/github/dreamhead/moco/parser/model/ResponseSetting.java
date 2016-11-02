package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.parser.ResponseHandlerFactory;
import com.google.common.base.MoreObjects;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponseSetting extends BaseResourceSetting {
    private final ResponseHandlerFactory factory = new DynamicResponseHandlerFactory();

    private String status;
    private ProxyContainer proxy;
    private Map<String, TextContainer> headers;
    private Map<String, CookieContainer> cookies;
    private LatencyContainer latency;

    private TextContainer version;
    private AttachmentSetting attachment;

    public ResponseSetting asResponseSetting() {
        ResponseSetting responseSetting = new ResponseSetting();
        responseSetting.text = text;
        responseSetting.file = file;
        responseSetting.pathResource = pathResource;
        responseSetting.status = status;
        responseSetting.proxy = proxy;
        responseSetting.headers = headers;
        responseSetting.cookies = cookies;
        responseSetting.latency = latency;
        responseSetting.version = version;
        responseSetting.json = json;
        responseSetting.attachment = attachment;

        return responseSetting;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("file", file)
                .add("version", version)
                .add("status", status)
                .add("headers", headers)
                .add("cookies", cookies)
                .add("proxy", proxy)
                .add("latency", latency)
                .add("path resource", pathResource)
                .add("json", json)
                .add("attachment", attachment)
                .toString();
    }

    public ResponseHandler getResponseHandler() {
        return factory.createResponseHandler(this);
    }
}
