package com.github.dreamhead.moco.matcher;

import com.google.common.base.Supplier;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

public class ConditionalRequestMatcher extends AbstractRequestMatcher {
	private final Supplier<Boolean> condition;
	
	public ConditionalRequestMatcher(final Supplier<Boolean> condition) {
		this.condition = condition;
	}
	
	@Override
	public boolean match(final Request request) {
		return condition.get();
	}

	@Override
	public RequestMatcher doApply(final MocoConfig config) {
		return this;
	}

}
