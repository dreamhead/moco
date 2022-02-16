package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.HttpHeader;
import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpResponse;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ArrayListMultimap;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MocoRequestAction implements MocoEventAction {
    private static Logger logger = LoggerFactory.getLogger(MocoRequestAction.class);
    private static final Dumper<Response> responseDumper = new HttpResponseDumper();
    private static final Dumper<Request> requestDumper = new HttpRequestDumper();

    private final Resource url;
    private final HttpHeader[] headers;


    protected abstract HttpUriRequest createRequest(String url, Request request);

    protected MocoRequestAction(final Resource url, final HttpHeader[] headers) {
        this.url = url;
        this.headers = headers;
    }

    @Override
    public final void execute(final Request request) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpUriRequest actionRequest = prepareRequest(request);
            dump(actionRequest);
            final CloseableHttpResponse response = client.execute(actionRequest);
            dump(response);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    private void dump(final HttpUriRequest request) {
        final URIBuilder uriBuilder = new URIBuilder(request.getURI());
        final DefaultHttpRequest.Builder builder = DefaultHttpRequest.builder()
                .withVersion(HttpProtocolVersion.versionOf(request.getProtocolVersion().toString()))
                .withUri(toPath(request.getURI()))
                .withQueries(asQueries(uriBuilder.getQueryParams()))
                .withMethod(HttpMethod.valueOf(request.getMethod().toUpperCase()))
                .withHeaders(asHeaders(request.getAllHeaders()));

        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
            try {
                builder.withContent(MessageContent.content()
                                .withContent(entityRequest.getEntity().getContent())
                                .build());
            } catch (IOException e) {
                // IGNORE
            }
        }

        logger.info("Action Request:{}\n", requestDumper.dump(builder.build()));
    }

    private static String toPath(final URI uri) {
        final String path = uri.toString();
        final int index = path.indexOf("?");
        if (index >= 0) {
            return path.substring(0, index);
        }

        return path;
    }

    private Map<String, String[]> asQueries(final List<NameValuePair> queries) {
        final HashMap<String, List<String>> map = new HashMap<>();
        final ArrayListMultimap<String, String> multimap = ArrayListMultimap.create();
        for (NameValuePair query : queries) {
            multimap.put(query.getName(), query.getValue());
        }

        Map<String, String[]> result = new HashMap<>();

        for (String key : multimap.keys()) {
            final List<String> strings = multimap.get(key);
            result.put(key, strings.toArray(new String[0]));
        }

        return result;
    }

    private void dump(final CloseableHttpResponse response) throws IOException {
        final DefaultHttpResponse dumped = DefaultHttpResponse.builder()
                .withVersion(HttpProtocolVersion.versionOf(response.getProtocolVersion().toString()))
                .withStatus(response.getStatusLine().getStatusCode())
                .withHeaders(asHeaders(response.getAllHeaders()))
                .withContent(MessageContent.content().withContent(response.getEntity().getContent()).build())
                .build();
        logger.info("Action Response: {}\n", responseDumper.dump(dumped));
    }

    private Map<String, String> asHeaders(Header[] allHeaders) {
        return Arrays.stream(allHeaders)
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
    }

    private HttpUriRequest prepareRequest(final Request request) {
        HttpUriRequest httpRequest = createRequest(url.readFor(request).toString(), request);
        for (HttpHeader header : headers) {
            httpRequest.addHeader(header.getName(), header.getValue().readFor(request).toString());
        }

        return httpRequest;
    }

    protected final Resource applyUrl(final MocoConfig config) {
        return this.url.apply(config);
    }

    protected final boolean isSameUrl(final Resource url) {
        return this.url == url;
    }

    protected final HttpHeader[] applyHeaders(final MocoConfig config) {
        HttpHeader[] appliedHeaders = new HttpHeader[this.headers.length];
        boolean applied = false;
        for (int i = 0; i < headers.length; i++) {
            HttpHeader appliedHeader = headers[i].apply(config);
            if (!headers[i].equals(appliedHeader)) {
                applied = true;
            }
            appliedHeaders[i] = appliedHeader;
        }

        if (applied) {
            return appliedHeaders;
        }

        return this.headers;
    }

    protected final boolean isSameHeaders(final HttpHeader[] headers) {
        return this.headers == headers;
    }
}
