package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.collect.ImmutableMap;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
    private final ImmutableMap<String, String> variables;

    public TemplateResourceReader(ContentResource template, ImmutableMap<String, String> variables) {
        this.template = template;
        this.variables = variables;
    }

    @Override
    public byte[] readFor(HttpRequest request) {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        String templateSource = new String(this.template.readFor(request));
        templateLoader.putTemplate(TEMPLATE_NAME, templateSource);
        Configuration cfg = createConfiguration(templateLoader);

        try {
            Template template = cfg.getTemplate(TEMPLATE_NAME);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            template.process(variables(request), writer);
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

    private Configuration createConfiguration(StringTemplateLoader templateLoader) {
        Configuration cfg = new Configuration();
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        cfg.setTemplateLoader(templateLoader);
        return cfg;
    }

    private ImmutableMap<String, Object> variables(HttpRequest request) {
        return ImmutableMap.<String, Object>builder().putAll(this.variables).put("req", request).build();
    }

    @Override
    public String getContentType() {
        return template.getContentType();
    }
}
