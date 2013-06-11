package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.google.common.io.Files;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DefaultFailover implements Failover {
    private Dumper<HttpResponse> httpResponseDumper = new HttpResponseDumper();

    private final File file;

    public DefaultFailover(File file) {
        this.file = file;
    }

    public void onCompleteResponse(HttpResponse response) {
        String dumpedResponse = httpResponseDumper.dump(response);
        try {
            Files.write(dumpedResponse, file, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
