package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.XmlExtractorHelper;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.FullHttpRequest;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

public class XmlRequestMatcher implements RequestMatcher {
    private final XmlExtractorHelper helper = new XmlExtractorHelper();
    private final DocumentBuilder documentBuilder;
    private final RequestExtractor<String> extractor;
    private final Resource resource;

    public XmlRequestMatcher(RequestExtractor<String> extractor, Resource resource) {
        this.extractor = extractor;
        this.resource = resource;
        this.documentBuilder = documentBuilder();
    }

    @Override
    public boolean match(HttpRequest request) {
        try {
            Document requestDocument = extractDocument(request, extractor);
            Document resourceDocument = getResourceDocument(null, this.resource);
            return requestDocument.isEqualNode(resourceDocument);
        } catch (SAXException e) {
            return false;
        }
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        if (config.isFor(resource.id())) {
            return new XmlRequestMatcher(this.extractor, resource.apply(config));
        }

        return this;
    }

    private Document getResourceDocument(FullHttpRequest request, Resource resource) throws SAXException {
        ByteArrayInputStream stream = new ByteArrayInputStream(resource.readFor(request));
        return extractDocument(new InputSource(stream), this);
    }

    private Document extractDocument(HttpRequest request, RequestExtractor<String> extractor) throws SAXException {
        return extractDocument(helper.extractAsInputSource(request, extractor), this);
    }

    public void trimChild(Node node, Node child) {
        if (child instanceof Text) {
            if (isNullOrEmpty(child.getNodeValue().trim())) {
                node.removeChild(child);
            }
            return;
        }

        if (child instanceof Element) {
            trimNode(child);
        }
    }

    // Whitespace will be kept by DOM parser.
    private void trimNode(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            trimChild(node, children.item(i));
        }
    }

    public DocumentBuilder documentBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);

        try {
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public Document extractDocument(InputSource inputSource, XmlRequestMatcher xmlRequestMatcher) throws SAXException {
        try {
            Document document = xmlRequestMatcher.documentBuilder.parse(inputSource);
            document.normalizeDocument();
            trimNode(document);
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
