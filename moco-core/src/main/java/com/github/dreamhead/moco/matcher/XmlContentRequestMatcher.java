package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.resource.Resource;
import org.w3c.dom.Document;

public final class XmlContentRequestMatcher extends XmlRequestMatcher {
    public XmlContentRequestMatcher(final Resource resource, final ContentRequestExtractor extractor) {
        super(resource, extractor);
    }

    @Override
    protected boolean doMatch(final Document actual, final Document expected) {
        return expected.isEqualNode(actual);
    }

    @Override
    protected RequestMatcher newAppliedMatcher(final Resource applied, final ContentRequestExtractor extractor) {
        return new XmlContentRequestMatcher(applied, extractor);
    }
}
