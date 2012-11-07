package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.RequestExtractor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class XPathRequestExtractor implements RequestExtractor {
    private ContentRequestExtractor extractor = new ContentRequestExtractor();

    public XPathRequestExtractor(String xpath) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        javax.xml.xpath.XPath target = xPathfactory.newXPath();
        try {
            xPathExpression = target.compile(xpath);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    private XPathExpression xPathExpression;

    @Override
    public String extract(HttpRequest request) {
        try {
            return xPathExpression.evaluate(new InputSource(new StringReader(extractor.extract(request))));
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
