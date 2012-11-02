package com.github.moco.matcher;

public class ContentMatcher extends AbstractContentMatcher {
    private String expected;

    public ContentMatcher(byte[] content) {
        this.expected = new String(content);
    }

    protected boolean doMatch(String requestContent) {
        return expected.equals(requestContent);
    }
}
