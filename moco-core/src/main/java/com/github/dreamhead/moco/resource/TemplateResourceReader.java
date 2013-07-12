package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.model.MessageFactory;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.*;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.google.common.collect.ImmutableMap.of;

public class TemplateResourceReader implements ContentResourceReader {
    private static final String TEMPLATE_NAME = "template";
    private final ContentResource template;
    private final Configuration cfg;

    public TemplateResourceReader(ContentResource template) {
        this.template = template;
        this.cfg = new Configuration();
        this.cfg.setObjectWrapper(new DefaultObjectWrapper());
        this.cfg.setDefaultEncoding("UTF-8");
        this.cfg.setIncompatibleImprovements(new Version(2, 3, 20));
    }

    @Override
    public byte[] readFor(HttpRequest request) {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(TEMPLATE_NAME, new String(this.template.readFor(request)));
        cfg.setTemplateLoader(templateLoader);

        try {
            Template template = cfg.getTemplate("template");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            template.process(of("req", MessageFactory.createRequest(request)), writer);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return template.getContentType();
    }
}
