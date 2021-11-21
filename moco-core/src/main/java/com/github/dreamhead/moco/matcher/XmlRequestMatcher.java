package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.extractor.XmlExtractorHelper;
import com.github.dreamhead.moco.resource.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class XmlRequestMatcher extends AbstractRequestMatcher {
    protected abstract boolean doMatch(final Node actual, final Node expected);
    protected abstract RequestMatcher newAppliedMatcher(final Resource applied, final ContentRequestExtractor extractor);

    private final XmlExtractorHelper helper = new XmlExtractorHelper();
    private final ContentRequestExtractor extractor;
    private final Resource resource;

    public XmlRequestMatcher(final Resource resource, final ContentRequestExtractor extractor) {
        this.extractor = extractor;
        this.resource = resource;
    }

    @Override
    public final boolean match(final Request request) {
        Optional<Document> requestDocument = extractDocument(request, extractor);
        return requestDocument.filter(actual -> tryToMatch(request, actual)).isPresent();
    }

    private boolean tryToMatch(final Request request, final Document actual) {
        try {
            Document expected = getExpectedDocument(request, this.resource);
            return doMatch(actual, expected);
        } catch (SAXException e) {
            return false;
        }
    }

    @Override
    public final RequestMatcher doApply(final MocoConfig config) {
        if (config.isFor(resource.id())) {
            return newAppliedMatcher(resource.apply(config), this.extractor);
        }

        return this;
    }

    private Document getExpectedDocument(final Request request, final Resource resource) throws SAXException {
        InputStream stream = resource.readFor(request).toInputStream();
        return extractDocument(new InputSource(stream));
    }

    private Optional<Document> extractDocument(final Request request,
                                               final ContentRequestExtractor extractor) {
        Optional<InputSource> inputSourceOptional = helper.extractAsInputSource(request, extractor);
        return inputSourceOptional.map(this::extractDocument);
    }

    private void trimChild(final Node node, final Node child) {
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
    private void trimNode(final Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            trimChild(node, children.item(i));
        }
    }

    private DocumentBuilder documentBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);

        try {
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MocoException(e);
        }
    }

    private Document extractDocument(final InputSource inputSource) {
        try {
            DocumentBuilder builder = documentBuilder();
            Document document = builder.parse(inputSource);
            document.normalizeDocument();
            trimNode(document);
            return document;
        } catch (IOException | SAXException e) {
            throw new MocoException(e);
        }
    }
}
