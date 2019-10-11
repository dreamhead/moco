package com.github.dreamhead.moco;

import java.util.Optional;

public interface RestSetting {
    boolean isSimple();
    Optional<ResponseHandler> getMatched(RestIdMatcher resourceName, HttpRequest httpRequest);
}
