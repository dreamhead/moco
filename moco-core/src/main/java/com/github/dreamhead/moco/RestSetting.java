package com.github.dreamhead.moco;

import com.google.common.base.Optional;

public interface RestSetting {
    boolean isSimple();
    Optional<ResponseHandler> getMatched(final RestIdMatcher resourceName, final HttpRequest httpRequest);
}
