package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.collect.ImmutableMap.copyOf;

public class TemplateResourceReader implements ContentResourceReader {
    private static final Version CURRENT_VERSION = Configuration.getVersion();
    private static final String TEMPLATE_NAME = "template";
    private static final List<String> RESERVED_NAME = ImmutableList.of("req", "now", "random");

    private static Logger logger = LoggerFactory.getLogger(TemplateResourceReader.class);

    static {
        System.setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,
                freemarker.log.Logger.LIBRARY_NAME_NONE);
    }

    private final ContentResource template;
    private final ImmutableMap<String, ? extends Variable> variables;


    public static String checkValidVariableName(final String name) {
        if (!RESERVED_NAME.contains(
                checkNotNullOrEmpty(name, "Template variable name should not be null"))) {
            return name;
        }

        throw new IllegalArgumentException("Template variable name should not be same with reserved name");
    }

    public TemplateResourceReader(final ContentResource template,
                                  final ImmutableMap<String, ? extends Variable> variables) {
        this.template = template;
        this.variables = variables;
    }

    @Override
    public final MessageContent readFor(final Request request) {
        if (request == null) {
            throw new IllegalStateException("Request is required to render template");
        }

        MessageContent content = this.template.readFor(request);

        try {
            Template targetTemplate = createTemplate(content);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(stream);
            targetTemplate.process(variables(request), writer);
            return content().withContent(stream.toByteArray()).build();
        } catch (ParseException e) {
            logger.error("Fail to parse template: {}", content.toString());
            throw new MocoException(e);
        } catch (IOException | TemplateException e) {
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
                .put("now", new NowMethod())
                .put("random", new RandomMethod())
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
    public final MediaType getContentType(final HttpRequest request) {
        return template.getContentType(request);
    }

    private static class NowMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(final List arguments) {
            if (arguments.size() < 1) {
                throw new IllegalArgumentException("Date format is required");
            }

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat(arguments.get(0).toString());
            return format.format(date);
        }
    }

    private static class RandomMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(final List arguments) {
            Optional<Long> range = getRange(arguments);
            Optional<? extends NumberFormat> format = getFormat(arguments);
            double result = new Random().nextDouble() * range.or(1L);

            if (format.isPresent()) {
                return format.get().format(result);
            }

            return result;
        }

        private Optional<? extends NumberFormat> getFormat(final List<?> arguments) {
            if (arguments.size() > 0) {
                Object last = arguments.get(arguments.size() - 1);
                if (last instanceof SimpleScalar) {
                    SimpleScalar lastArgument = (SimpleScalar) last;
                    return Optional.of(new DecimalFormat(lastArgument.toString()));
                }
            }

            return Optional.absent();
        }

        private Optional<Long> getRange(final List<?> arguments) {
            if (arguments.size() > 0) {
                Object range = arguments.get(0);
                if (range instanceof SimpleNumber) {
                    return getRange((SimpleNumber) range);
                }
            }

            return Optional.absent();
        }

        private Optional<Long> getRange(final SimpleNumber range) {
            long reference = range.getAsNumber().longValue();
            if (reference <= 0) {
                throw new IllegalArgumentException("Random range should be greater than 0");
            }

            return Optional.of(reference);
        }
    }
}
