package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.Optional;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class XmlExtractorHelper {
    public final Optional<InputSource> extractAsInputSource(final Request request,
                                                            final ContentRequestExtractor extractor) {
        Optional<MessageContent> content = extractor.extract(request);
        if (content.isPresent()) {
            return of(new InputSource(new ByteArrayInputStream(content.get().getContent())));
        }

        return absent();
    }
}
