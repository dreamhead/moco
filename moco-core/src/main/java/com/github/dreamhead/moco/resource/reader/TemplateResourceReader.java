package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.collect.ImmutableMap.copyOf;

public class TemplateResourceReader implements ContentResourceReader {
    private static final Version CURRENT_VERSION = Configuration.getVersion();
    private static final String TEMPLATE_NAME = "template";

    private static Logger logger = LoggerFactory.getLogger(TemplateResourceReader.class);

    static {
        System.setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,
                freemarker.log.Logger.LIBRARY_NAME_NONE);
    }

    private final ContentResource template;
    private final ImmutableMap<String, ? extends Variable> variables;

    public TemplateResourceReader(final ContentResource template,
                                  final ImmutableMap<String, ? extends Variable> variables) {
        this.template = template;
        this.variables = variables;
    }

    @Override
    public MessageContent readFor(final Optional<? extends Request> request) {
        if (!request.isPresent()) {
            throw new IllegalStateException("Request is required to render template");
        }

        MessageContent content = this.template.readFor(request);

        try {
            Template targetTemplate = createTemplate(content);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            targetTemplate.process(variables(request.get()), writer);
            return content().withContent(stream.toByteArray()).build();
        } catch (ParseException e) {
            logger.error("Fail to parse template: {}", content.toString());
            throw new MocoException(e);
        } catch (IOException e) {
            throw new MocoException(e);
        } catch (TemplateException e) {
            throw new MocoException(e);
        }
    }

    private Template createTemplate(final MessageContent messageContent) throws IOException {
        TemplateLoader templateLoader = createTemplateLoader(messageContent);
        Configuration cfg = createConfiguration(templateLoader, messageContent.getCharset());
        return cfg.getTemplate(TEMPLATE_NAME);
    }

    private StringTemplateLoader createTemplateLoader(final MessageContent messageContent) {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(TEMPLATE_NAME, messageContent.toString());
        return templateLoader;
    }

    private Configuration createConfiguration(final TemplateLoader templateLoader, final Charset charset) {
        Configuration cfg = new Configuration(CURRENT_VERSION);
        cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(CURRENT_VERSION).build());
        cfg.setDefaultEncoding(charset.name());
        cfg.setTemplateLoader(templateLoader);
        return cfg;
    }

    private ImmutableMap<String, Object> variables(final Request request) {
        return ImmutableMap.<String, Object>builder()
                .putAll(toVariableString(request))
                .put("req", toTemplateRequest(request))
                .build();
    }

    private TemplateRequest toTemplateRequest(final Request request) {
        return new TemplateRequest(request);
    }

    private ImmutableMap<String, Object> toVariableString(final Request request) {
        return copyOf(Maps.transformEntries(this.variables, new Maps.EntryTransformer<String, Variable, Object>() {
            @Override
            public Object transformEntry(final String key, final Variable value) {
                return value.toTemplateVariable(request);
            }
        }));
    }

    @Override
    public MediaType getContentType(final HttpRequest request) {
        return template.getContentType(request);
    }
}
