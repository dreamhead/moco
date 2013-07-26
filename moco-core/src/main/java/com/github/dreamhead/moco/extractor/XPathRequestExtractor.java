package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import io.netty.handler.codec.http.FullHttpRequest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class XPathRequestExtractor implements RequestExtractor<String[]> {
    private final XmlExtractorHelper helper = new XmlExtractorHelper();
    private final ContentRequestExtractor extractor = new ContentRequestExtractor();
    private final XPathExpression xPathExpression;

    public XPathRequestExtractor(String xpath) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath target = xPathfactory.newXPath();
        try {
            xPathExpression = target.compile(xpath);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String[] extract(FullHttpRequest request) {
        try {
            NodeList list = (NodeList) xPathExpression.evaluate(helper.extractAsInputSource(request, extractor), XPathConstants.NODESET);
            List<String> values = newArrayList();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                values.add(node.getNodeValue());
            }
            return values.toArray(new String[values.size()]);
        } catch (XPathExpressionException e) {
            return new String[0];
        }
    }
}
