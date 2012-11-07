package com.github.dreamhead.moco;

import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.extractor.HeaderRequestExtractor;
import com.github.dreamhead.moco.extractor.UriRequestExtractor;
import com.github.dreamhead.moco.extractor.XPathRequestExtractor;
import com.github.dreamhead.moco.handler.SequenceResponseHandler;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.EqRequestMatcher;
import com.github.dreamhead.moco.matcher.OrRequestMatcher;
import com.github.dreamhead.moco.model.ContentStream;

import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Moco {
    public static HttpServer httpserver(int port) {
        return new HttpServer(port);
    }

    public static RequestMatcher by(String content) {
        return by(text(content));
    }

    public static RequestMatcher by(ContentStream stream) {
        return eq(new ContentRequestExtractor(), new String(stream.asByteArray()));
    }

    public static RequestMatcher by(Expectation expectation) {
        return eq(expectation.getExtractor(), expectation.getExpected());
    }

    public static RequestMatcher eq(RequestExtractor extractor, String expected) {
        return new EqRequestMatcher(extractor, expected);
    }

    public static RequestMatcher and(RequestMatcher... matchers) {
        return new AndRequestMatcher(matchers);
    }

    public static RequestMatcher or(RequestMatcher... matchers) {
        return new OrRequestMatcher(matchers);
    }

    public static ContentStream text(String text) {
        return new ContentStream(text);
    }

    public static Expectation uri(String uri) {
        return new Expectation(new UriRequestExtractor(), uri);
    }

    public static RequestExtractor header(String header) {
        return new HeaderRequestExtractor(header);
    }

    public static XPathRequestExtractor xpath(String xpath) {
        return new XPathRequestExtractor(xpath);
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

    public static void running(HttpServer httpServer, Runnable runnable) {
        MocoHttpServer server = new MocoHttpServer(httpServer);
        try {
            server.start();
            runnable.run();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            throw new RuntimeException(t);
        } finally {
            server.stop();
        }
    }
}
