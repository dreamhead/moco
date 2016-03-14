package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.google.common.base.Optional;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class XmlExtractorHelper {
    public Optional<InputSource> extractAsInputSource(final Request request, final RequestExtractor<byte[]> extractor) {
        Optional<byte[]> content = extractor.extract(request);
        if (content.isPresent()) {
            return of(new InputSource(new ByteArrayInputStream(content.get())));
        }

        return absent();
    }
}
