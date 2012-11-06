package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.model.XPath;

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
