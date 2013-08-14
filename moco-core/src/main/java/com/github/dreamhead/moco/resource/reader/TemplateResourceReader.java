package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.model.LazyRequest;
import com.github.dreamhead.moco.resource.ContentResource;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.google.common.collect.ImmutableMap.of;

public class TemplateResourceReader implements ContentResourceReader {
    private static final Logger logger = LoggerFactory.getLogger(TemplateResourceReader.class);
    private static final String TEMPLATE_NAME = "template";

    static {
        try {
            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
    public byte[] readFor(FullHttpRequest request) {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        String templateSource = new String(this.template.readFor(request));
        templateLoader.putTemplate(TEMPLATE_NAME, templateSource);
        cfg.setTemplateLoader(templateLoader);

        try {
            Template template = cfg.getTemplate("template");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            template.process(of("req", new LazyRequest(request)), writer);
            return stream.toByteArray();
        } catch (ParseException e) {
            logger.info("Template is {}", templateSource);
            throw new RuntimeException(e);
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
