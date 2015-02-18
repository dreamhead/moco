package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.google.common.collect.Iterators;

import java.io.IOException;
import java.util.Iterator;

import static com.github.dreamhead.moco.parser.model.FileContainer.asFileContainer;

public class FileContainerDeserializer extends AbstractTextContainerDeserializer<FileContainer> {
    @Override
    public FileContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return asFileContainer(text(jp));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();

            String target = jp.getText().trim();
            if (isForFileContainer(target)) {
                Iterator<FileVar> iterator = jp.readValuesAs(FileVar.class);
                FileVar file = Iterators.get(iterator, 0);
                return FileContainer.aFileContainer().withName(file.name).withCharset(file.charset).build();
            }

            return asFileContainer(textContainer(jp, ctxt));
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }

    private boolean isForFileContainer(String target) {
        return "name".equalsIgnoreCase(target) || "charset".equalsIgnoreCase(target);
    }


    private static class FileVar {
        public String name;
        public String charset;
    }
}
