package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class XmlExtractorHelper {
    public final Optional<InputSource> extractAsInputSource(final Request request,
                                                            final ContentRequestExtractor extractor) {
        Optional<MessageContent> content = extractor.extract(request);
        if (content.isPresent()) {
            return of(new InputSource(new ByteArrayInputStream(content.get().getContent())));
        }

        return empty();
    }
}
