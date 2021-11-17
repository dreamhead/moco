package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import org.w3c.dom.Document;

public class XmlContentRequestMatcher extends XmlRequestMatcher {
    public XmlContentRequestMatcher(Resource resource) {
        super(resource);
    }

    @Override
    protected boolean doMatch(final Document actual, final Document expected) {
        return expected.isEqualNode(actual);
    }

    @Override
    protected RequestMatcher newAppliedMatcher(final Resource applied) {
        return new XmlContentRequestMatcher(applied);
    }
}
