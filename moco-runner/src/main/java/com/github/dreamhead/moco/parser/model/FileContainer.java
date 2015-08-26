package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.FileContainerDeserializer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

@JsonDeserialize(using = FileContainerDeserializer.class)
public final class FileContainer extends TextContainer {
    private TextContainer name;
    private Optional<Charset> charset;

    private FileContainer(final TextContainer container) {
        super(container.getText(), container.getOperation(), container.getProps());
    }

    private FileContainer(final TextContainer name, final Optional<Charset> charset) {
        this.name = name;
        this.charset = charset;
    }

    public TextContainer getName() {
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

    public static FileContainer asFileContainer(final TextContainer container) {
        return new FileContainer(container);
    }

    public static FileContainerBuilder aFileContainer() {
        return new FileContainerBuilder();
    }

    public static class FileContainerBuilder {
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
            return new FileContainer(name, toCharset(charset));
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
