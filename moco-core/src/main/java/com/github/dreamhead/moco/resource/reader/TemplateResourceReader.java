package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;
import freemarker.cache.StringTemplateLoader;
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
        System.setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY, freemarker.log.Logger.LIBRARY_NAME_NONE);
    }

    private final ContentResource template;
    private final ImmutableMap<String, ? extends Variable> variables;

    public TemplateResourceReader(final ContentResource template, final ImmutableMap<String, ? extends Variable> variables) {
        this.template = template;
        this.variables = variables;
    }

    @Override
    public MessageContent readFor(final Optional<? extends Request> request) {
        if (!request.isPresent()) {
            throw new IllegalArgumentException("Request is required to render template");
        }

        StringTemplateLoader templateLoader = new StringTemplateLoader();
        MessageContent messageContent = this.template.readFor(request);
        String templateSource = messageContent.toString();
        templateLoader.putTemplate(TEMPLATE_NAME, templateSource);
        Configuration cfg = createConfiguration(templateLoader, messageContent.getCharset());

        try {
            Template template = cfg.getTemplate(TEMPLATE_NAME);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            template.process(variables(request.get()), writer);

            return content().withContent(stream.toByteArray()).build();
        } catch (ParseException e) {
            logger.error("Fail to parse template: {}", templateSource);
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private Configuration createConfiguration(final StringTemplateLoader templateLoader, final Charset charset) {
        Configuration cfg = new Configuration(CURRENT_VERSION);
        cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(CURRENT_VERSION).build());
        cfg.setDefaultEncoding(charset.name());
        cfg.setTemplateLoader(templateLoader);
        return cfg;
    }

    private ImmutableMap<String, Object> variables(final Request request) {
        return ImmutableMap.<String, Object>builder().putAll(toVariableString(request)).put("req", toTemplateRequest(request)).build();
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

    public static class TemplateRequest {
        private Request request;

        public TemplateRequest(final Request request) {
            this.request = request;
        }

        public MessageContent getContent() {
            return this.request.getContent();
        }

        public HttpProtocolVersion getVersion() {
            if (this.request instanceof HttpRequest) {
                return ((HttpRequest) this.request).getVersion();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }

        public ImmutableMap<String, String> getHeaders() {
            if (this.request instanceof HttpRequest) {
                return ((HttpRequest) this.request).getHeaders();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }

        public String getUri() {
            if (this.request instanceof HttpRequest) {
                return ((HttpRequest) this.request).getUri();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }

        public String getMethod() {
            if (this.request instanceof HttpRequest) {
                return ((HttpRequest) this.request).getMethod();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }

        public ImmutableMap<String, String> getQueries() {
            if (this.request instanceof HttpRequest) {
                HttpRequest httpRequest = (HttpRequest) this.request;
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                ImmutableMap<String, String[]> queries = httpRequest.getQueries();
                for (String key : queries.keySet()) {
                    builder.put(key, queries.get(key)[0]);
                }

                return builder.build();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }

        public ImmutableMap<String, String> getForms() {
            if (this.request instanceof DefaultHttpRequest) {
                return ((DefaultHttpRequest) this.request).getForms();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }

        public ImmutableMap<String, String> getCookies() {
            if (this.request instanceof DefaultHttpRequest) {
                return ((DefaultHttpRequest) this.request).getCookies();
            }

            throw new IllegalArgumentException("Request is not HTTP request");
        }
    }
}
