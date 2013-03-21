package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class XmlExtractorHelper {
    public InputSource extractAsInputSource(HttpRequest request, RequestExtractor extractor) {
        return new InputSource(new StringReader(extractor.extract(request)));
    }
}
