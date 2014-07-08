package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class XmlExtractorHelper {
    public InputSource extractAsInputSource(Request request, RequestExtractor<String> extractor) {
        return new InputSource(new StringReader(extractor.extract(request).get()));
    }
}
