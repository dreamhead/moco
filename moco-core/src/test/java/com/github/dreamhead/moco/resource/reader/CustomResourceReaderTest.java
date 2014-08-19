package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.junit.Test;


public class CustomResourceReaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_argument_exception_if_request_is_absent() {
        final CustomResourceReader customResourceReader = new CustomResourceReader(new Function<Request, String>() {
            @Override
            public String apply(Request input) {
                return null;
            }
        });

        customResourceReader.readFor(Optional.<Request>absent());
    }

}
