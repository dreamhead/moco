package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.FileContainerDeserializer;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

@JsonDeserialize(using = FileContainerDeserializer.class)
public final class FileContainer extends TextContainer {
    private TextContainer name;
    private Charset charset;
    private TextContainer content;

    private FileContainer(final TextContainer container) {
        this.name = container;
        this.content = container;
    }

    public TextContainer getName() {
        return name;
    }

    public Optional<Charset> getCharset() {
        return Optional.fromNullable(charset);
    }

    @Override
    public ContentResource asResource() {
        if (this.content == null) {
            return null;
        }

        return this.content.asResource();
    }

    @Override
    public ContentResource asTemplateResource() {
        if (this.content == null) {
            return null;
        }

        return this.content.asTemplateResource();
    }

    @Override
    public ContentResource asTemplateResource(final String resourceName) {
        if (this.content == null) {
            return null;
        }

        return this.content.asTemplateResource(resourceName);
    }

    @Override
    public boolean isRawText() {
        return this.content != null && this.content.isRawText();

    }

    @Override
    public String getText() {
        if (this.content == null) {
            return null;
        }

        return this.content.getText();
    }

    @Override
    public String getOperation() {
        if (this.content == null) {
            return null;
        }

        return this.content.getOperation();
    }

    @Override
    public boolean hasProperties() {
        return this.content != null && this.content.hasProperties();
    }

    @Override
    public Map<String, TextContainer> getProps() {
        if (this.content == null) {
            return null;
        }

        return this.content.getProps();
    }

    @Override
    public boolean isForTemplate() {
        return this.content != null && this.content.isForTemplate();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("name", name)
                .add("charset", charset);
    }

    @Override
    public boolean isFileContainer() {
        return true;
    }

    public static FileContainer asFileContainer(final TextContainer container) {
        return new FileContainer(container);
    }

    public static FileContainerBuilder aFileContainer() {
        return new FileContainerBuilder();
    }

    public static final class FileContainerBuilder {
        private TextContainer name;
        private String charset;

        public FileContainerBuilder withName(final TextContainer name) {
            this.name = name;
            return this;
        }

        public FileContainerBuilder withCharset(final String charset) {
            this.charset = charset;
            return this;
        }

        public FileContainer build() {
            FileContainer container = new FileContainer(name);
            container.charset = toCharset(charset).orNull();
            if (charset == null) {
                container.content = null;
            }
            return container;
        }

        private Optional<Charset> toCharset(final String charset) {
            if (charset == null) {
                return absent();
            }

            try {
                return of(Charset.forName(charset));
            } catch (UnsupportedCharsetException e) {
                return absent();
            }
        }
    }
}
