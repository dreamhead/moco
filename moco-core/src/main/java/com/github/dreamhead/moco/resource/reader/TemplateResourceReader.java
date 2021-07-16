package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
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
import java.util.Optional;
import java.util.Random;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;
import static com.google.common.base.Preconditions.checkArgument;
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
            logger.warn("Fail to parse template: {}", content.toString());
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
        return copyOf(Maps.transformEntries(this.variables, (key, value) -> value.toTemplateVariable(request)));
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

    private static class Range {
        private Optional<Long> start;
        private Optional<Long> end;

        Range(final Optional<Long> start, final Optional<Long> end) {
            this.start = start;
            this.end = end;
        }

        private double random() {
            final Long startValue = start.orElse(0L);
            final Long endValue = end.orElse(1L);
            final long range = endValue - startValue;
            return startValue + new Random().nextDouble() * range;
        }
    }

    private static class RandomMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(final List arguments) {
            final Range range = getRange(arguments);
            Optional<? extends NumberFormat> format = getFormat(arguments);
            double result = range.random();

            return format.map(actual -> (Object)actual.format(result)).orElse(result);
        }

        private Range getRange(final List<?> arguments) {
            if (arguments.size() <= 0) {
                return new Range(Optional.empty(), Optional.empty());
            }

            if (arguments.size() == 1) {
                return singleArgRange(arguments);
            }

            return moreArgRange(arguments);
        }

        private Range singleArgRange(final List<?> arguments) {
            final Object end = arguments.get(0);
            if (end instanceof SimpleNumber) {
                return getSingleRange((SimpleNumber) end);
            }

            return new Range(Optional.empty(), Optional.empty());
        }

        private Range moreArgRange(final List<?> arguments) {
            final Object start = arguments.get(0);
            final Object end = arguments.get(1);
            if (start instanceof SimpleNumber && end instanceof SimpleNumber) {
                return getDoubleRange((SimpleNumber) start, (SimpleNumber) end);
            }

            if (start instanceof SimpleNumber) {
                return getSingleRange((SimpleNumber) start);
            }

            throw new IllegalArgumentException("Range should be number");
        }

        private Range getDoubleRange(final SimpleNumber start, final SimpleNumber end) {
            final long startValue = getValidValue(start, "Start");
            final long endValue = getValidValue(end, "End");
            checkArgument(endValue > startValue, "Range should be greater than 0");
            return new Range(Optional.of(startValue), Optional.of(endValue));
        }

        private Range getSingleRange(final SimpleNumber end) {
            return new Range(Optional.empty(), Optional.of(getValidValue(end, "Range")));
        }

        private long getValidValue(final SimpleNumber number, final String name) {
            final long value = number.getAsNumber().longValue();
            checkArgument(value > 0, name + " should be greater than 0");
            return value;
        }

        private Optional<? extends NumberFormat> getFormat(final List<?> arguments) {
            if (arguments.size() <= 0) {
                return Optional.empty();
            }

            Object last = arguments.get(arguments.size() - 1);
            if (last instanceof SimpleScalar) {
                SimpleScalar lastArgument = (SimpleScalar) last;
                return Optional.of(new DecimalFormat(lastArgument.toString()));
            }

            return Optional.empty();
        }
    }
}
