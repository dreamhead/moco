package com.github.moco;

import com.github.moco.handler.SequenceResponseHandler;
import com.github.moco.internal.MocoHttpServer;
import com.github.moco.matcher.ContentMatcher;
import com.github.moco.matcher.UriRequestMatcher;
import com.github.moco.model.ContentStream;
import com.github.moco.model.Uri;

import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Moco {
    public static MocoHttpServer httpserver(int port) {
        return new MocoHttpServer(port);
    }

    public static RequestMatcher eq(String content) {
        return eq(text(content));
    }

    public static RequestMatcher eq(ContentStream stream) {
        return new ContentMatcher(stream.asByteArray());
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
        List<ContentStream> streams = newArrayList();
        for (String content : contents) {
            streams.add(text(content));
        }

        return new SequenceResponseHandler(streams.toArray(new ContentStream[streams.size()]));
    }

    public static ResponseHandler seq(ContentStream... contents) {
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
