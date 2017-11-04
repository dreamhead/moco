package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;

import static com.github.dreamhead.moco.parser.model.FileContainer.aFileContainer;
import static com.github.dreamhead.moco.parser.model.FileContainer.asFileContainer;
import static com.github.dreamhead.moco.util.Strings.strip;

public final class FileContainerDeserializer extends JsonDeserializer<FileContainer> {
    private TextContainerDeserializerHelper helper = new TextContainerDeserializerHelper();

    @Override
    public FileContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return asFileContainer(helper.text(jp));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();

            String target = strip(jp.getText());
            if (isForFileContainer(target)) {
                return toFileContainer(jp);
            }

            return asFileContainer(helper.textContainer(jp, ctxt));
        }

        return (FileContainer) ctxt.handleUnexpectedToken(FileContainer.class, jp);
    }

    private FileContainer toFileContainer(final JsonParser jp) throws IOException {
        FileVar file = jp.readValueAs(FileVar.class);
        return file.toFileContainer();
    }

    private boolean isForFileContainer(final String target) {
        return "name".equalsIgnoreCase(target) || "charset".equalsIgnoreCase(target);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class FileVar {
        private TextContainer name;
        private String charset;

        private boolean isAllowedFilename(final TextContainer filename) {
            return filename.isRawText() || filename.isForTemplate();
        }

        public FileContainer toFileContainer() {
            if (!isAllowedFilename(name)) {
                throw new IllegalArgumentException("only string and template are allowed as filename");
            }

            return aFileContainer().withName(name).withCharset(charset).build();
        }
    }
}
