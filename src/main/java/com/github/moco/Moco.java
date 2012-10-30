package com.github.moco;

import com.github.moco.handler.SequenceResponseHandler;
import com.github.moco.internal.MocoHttpServer;
import com.github.moco.matcher.ContentMatcher;
import com.github.moco.matcher.UriRequestMatcher;
import com.github.moco.model.ContentStream;
import com.github.moco.model.Uri;

import java.io.InputStream;

public class Moco {
    public static MocoHttpServer httpserver(int port) {
        return new MocoHttpServer(port);
    }

    public static RequestMatcher eq(ContentStream stream) {
        return new ContentMatcher(stream.asInputStream());
    }

    public static RequestMatcher eq(Uri uri) {
        return new UriRequestMatcher(uri.getUri());
    }

    public static ContentStream text(String text) {
        return new ContentStream(text);
    }

    public static Uri uri(String uri) {
        return new Uri(uri);
    }

    public static ResponseHandler seq(String... contents) {
        return new SequenceResponseHandler(contents);
    }

    public static ContentStream stream(InputStream is) {
        return new ContentStream(is);
    }

    public static void running(MocoHttpServer httpServer, Runnable runnable) {
        try {
            httpServer.start();
            runnable.run();
        } finally {
            httpServer.stop();
        }
    }
}
