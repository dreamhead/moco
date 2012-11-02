package com.github.moco.model;

import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

public class XPath {
    private XPathExpression xPathExpression;

    public XPath(String xpath) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        javax.xml.xpath.XPath target = xPathfactory.newXPath();
        try {
            xPathExpression = target.compile(xpath);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public String eval(String content) {
        try {
            return xPathExpression.evaluate(new InputSource(new StringReader(content))).toString();
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
