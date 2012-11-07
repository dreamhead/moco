package com.github.dreamhead.moco;

public class Expectation {
    private RequestExtractor extractor;
    private String expected;

    public Expectation(RequestExtractor extractor, String expected) {
        this.extractor = extractor;
        this.expected = expected;
    }

    public RequestExtractor getExtractor() {
        return extractor;
    }

    public String getExpected() {
        return expected;
    }
}
