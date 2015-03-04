package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.FileContainerDeserializer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.nio.charset.Charset;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

@JsonDeserialize(using = FileContainerDeserializer.class)
public class FileContainer extends TextContainer {
    private String name;
    private Optional<Charset> charset;

    private FileContainer(TextContainer container) {
        super(container.getText(), container.getOperation(), container.getProps());
    }

    private FileContainer(String name, Optional<Charset> charset) {
        this.name = name;
        this.charset = charset;
    }

    public String getName() {
        return name;
    }

    public Optional<Charset> getCharset() {
        return charset;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("charset", charset)
                .toString();
    }

    @Override
    public boolean isFileContainer() {
        return name != null;
    }

    public static FileContainer asFileContainer(TextContainer container) {
        return new FileContainer(container);
    }

    public static FileContainerBuilder aFileContainer() {
        return new FileContainerBuilder();
    }

    public static class FileContainerBuilder {
        private String name;
        private String charset;

        public FileContainerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public FileContainerBuilder withCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public FileContainer build() {
            return new FileContainer(name, toCharset(charset));
        }

        private Optional<Charset> toCharset(String charset) {
            if (charset == null) {
                return absent();
            }
            return fromNullable(Charset.forName(charset));
        }
    }
}
