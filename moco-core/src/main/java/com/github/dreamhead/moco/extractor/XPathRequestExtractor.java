package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import com.google.common.base.Optional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;

public class XPathRequestExtractor extends HttpRequestExtractor<String[]> {
    private final XmlExtractorHelper helper = new XmlExtractorHelper();
    private final ContentRequestExtractor extractor = new ContentRequestExtractor();
    private final XPathExpression xPathExpression;

    public XPathRequestExtractor(final String xpath) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath target = xPathfactory.newXPath();
        try {
            xPathExpression = target.compile(xpath);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    protected Optional<String[]> doExtract(final HttpRequest request) {
        try {
            NodeList list = (NodeList) xPathExpression.evaluate(helper.extractAsInputSource(request, extractor), XPathConstants.NODESET);
            if (list.getLength() == 0) {
                return absent();
            }

            return doExtract(list);
        } catch (XPathExpressionException e) {
            return absent();
        }
    }

    private Optional<String[]> doExtract(NodeList list) {
        List<String> values = newArrayList();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            values.add(node.getNodeValue());
        }

        return of(values.toArray(new String[values.size()]));
    }
}
