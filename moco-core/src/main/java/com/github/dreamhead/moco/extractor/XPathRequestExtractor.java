package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpRequestExtractor;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.Optional.empty;
import static java.util.Optional.of;

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
    protected final Optional<String[]> doExtract(final HttpRequest request) {
        return helper.extractAsInputSource(request, extractor).flatMap(this::doExtract);
    }

    private Optional<String[]> doExtract(final InputSource source) {
        try {
            NodeList list = (NodeList) xPathExpression.evaluate(source, XPathConstants.NODESET);
            if (list.getLength() == 0) {
                return empty();
            }

            return doExtract(list);
        } catch (XPathExpressionException e) {
            return empty();
        }
    }

    private Optional<String[]> doExtract(final NodeList list) {
        return of(IntStream.range(0, list.getLength())
                .mapToObj(index -> list.item(index).getNodeValue())
                .toArray(String[]::new));
    }
}
