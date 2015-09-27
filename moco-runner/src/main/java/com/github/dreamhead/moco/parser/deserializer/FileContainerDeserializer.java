package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;
import java.util.Iterator;

import static com.github.dreamhead.moco.parser.model.FileContainer.aFileContainer;
import static com.github.dreamhead.moco.parser.model.FileContainer.asFileContainer;
import static com.google.common.collect.Iterators.get;

public class FileContainerDeserializer extends AbstractTextContainerDeserializer<FileContainer> {
    @Override
    public FileContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return asFileContainer(text(jp));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();

            String target = jp.getText().trim();
            if (isForFileContainer(target)) {
                return toFileContainer(jp);
            }

            return asFileContainer(textContainer(jp, ctxt));
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }

    private FileContainer toFileContainer(final JsonParser jp) throws IOException {
        Iterator<FileVar> iterator = jp.readValuesAs(FileVar.class);
        FileVar file = get(iterator, 0);
        TextContainer filename = file.name;
        if (!isAllowedFilename(filename)) {
            throw new IllegalArgumentException("only string and template are allowed as filename");
        }

        return aFileContainer().withName(filename).withCharset(file.charset).build();
    }

    private boolean isAllowedFilename(final TextContainer filename) {
        return filename.isRawText() || filename.isForTemplate();
    }

    private boolean isForFileContainer(final String target) {
        return "name".equalsIgnoreCase(target) || "charset".equalsIgnoreCase(target);
    }

    private static class FileVar {
        public TextContainer name;
        public String charset;
    }
}
