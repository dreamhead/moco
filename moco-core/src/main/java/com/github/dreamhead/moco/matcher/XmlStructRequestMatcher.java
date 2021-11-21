package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.resource.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlStructRequestMatcher extends XmlRequestMatcher {
    public XmlStructRequestMatcher(final Resource resource, final ContentRequestExtractor extractor) {
        super(resource, extractor);
    }

    @Override
    protected boolean doMatch(final Node actual, final Node expected) {
        if (actual.getNodeType() != expected.getNodeType()) {
            return false;
        }

        if (expected.getNodeType() == Node.DOCUMENT_NODE) {
            final Document actualDocument = (Document) actual;
            final Document expectedDocument = (Document) expected;
            return doMatch(actualDocument.getChildNodes(), expectedDocument.getChildNodes());
        }

        if (expected.getNodeType() == Node.ELEMENT_NODE) {
            final Element actualNode = (Element) actual;
            final Element expectedNode = (Element) expected;
            return actualNode.getNodeName().equals(expectedNode.getNodeName())
                    && doMatch(actualNode.getChildNodes(), expectedNode.getChildNodes());
        }

        return true;
    }

    private boolean doMatch(final NodeList actualNodes, final NodeList expectedNodes) {
        final int length = actualNodes.getLength();
        if (length != expectedNodes.getLength()) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!doMatch(actualNodes.item(i), expectedNodes.item(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected RequestMatcher newAppliedMatcher(Resource applied, ContentRequestExtractor extractor) {
        return null;
    }
}
