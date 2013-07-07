package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.model.MessageFactory;
import com.github.dreamhead.moco.resource.ContentResource;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.google.common.collect.ImmutableMap.of;

public class TemplateResponseHandler extends AbstractContentResponseHandler {
    private static final String TEMPLATE_NAME = "template";
    private final ContentResource template;
    private final Configuration cfg;

    public TemplateResponseHandler(ContentResource template) {
        this.template = template;
        this.cfg = new Configuration();
        this.cfg.setObjectWrapper(new DefaultObjectWrapper());
        this.cfg.setDefaultEncoding("UTF-8");
        this.cfg.setIncompatibleImprovements(new Version(2, 3, 20));
    }

    @Override
    protected void writeContentResponse(HttpRequest request, ChannelBuffer buffer) {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(TEMPLATE_NAME, new String(this.template.asByteArray()));
        cfg.setTemplateLoader(templateLoader);

        try {
            Template template = cfg.getTemplate("template");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            template.process(of("req", MessageFactory.createRequest(request)), writer);
            buffer.writeBytes(stream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseHandler apply(MocoConfig config) {
        return this;
    }

    @Override
    protected String getContentType(HttpRequest request) {
        return this.template.getContentType();
    }
}
