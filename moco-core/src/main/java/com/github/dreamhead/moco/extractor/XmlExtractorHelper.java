package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import org.xml.sax.InputSource;

import java.util.Optional;

public class XmlExtractorHelper {
    public final Optional<InputSource> extractAsInputSource(final Request request,
                                                            final ContentRequestExtractor extractor) {
        Optional<MessageContent> content = extractor.extract(request);
        return content.map(messageContent -> new InputSource(messageContent.toInputStream()));
    }
}
