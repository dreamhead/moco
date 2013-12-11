package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class XmlExtractorHelper {
    public InputSource extractAsInputSource(HttpRequest request, RequestExtractor<String> extractor) {
        return new InputSource(new StringReader(extractor.extract(request).get()));
    }
}
