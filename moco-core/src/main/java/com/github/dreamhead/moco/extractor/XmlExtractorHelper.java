package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

public class XmlExtractorHelper {
    public InputSource extractAsInputSource(final Request request, final RequestExtractor<byte[]> extractor) {
        return new InputSource(new ByteArrayInputStream(extractor.extract(request).get()));
    }
}
