package com.github.moco.matcher;

import com.github.moco.RequestMatcher;
import com.github.moco.model.XPath;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class XPathRequestMatcher extends AbstractContentMatcher {
    private final XPath xpath;
    private final String expected;

    public XPathRequestMatcher(XPath xpath, String expected) {
        this.xpath = xpath;
        this.expected = expected;
    }

    @Override
    protected boolean doMatch(String requestContent) {
        return expected.equals(xpath.eval(requestContent));
    }
}
