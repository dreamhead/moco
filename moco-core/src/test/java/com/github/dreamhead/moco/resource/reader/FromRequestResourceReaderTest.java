package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.junit.Test;


public class FromRequestResourceReaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_argument_exception_if_request_is_absent() {
        final FromRequestResourceReader fromRequestResourceReader = new FromRequestResourceReader(new Function<Request, String>() {
            @Override
            public String apply(Request input) {
                return null;
            }
        });

        fromRequestResourceReader.readFor(Optional.<Request>absent());
    }

}
