package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.AbstractMocoTest;
import com.github.dreamhead.moco.RemoteTestUtils;
import com.github.dreamhead.moco.Runnable;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CountingResponseHandlerTest extends AbstractMocoTest {

    @Test
    public void should_response_with_default_http_statuses() throws Exception {
        final int numberOfRequestsBeforeSuccessfulStatus = 5;
        server.response(new CountingResponseHandler(numberOfRequestsBeforeSuccessfulStatus));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                triggerRequestAndExpectDefaultFailureStatus(numberOfRequestsBeforeSuccessfulStatus);
                triggerRequestAndExpectDefaultSuccessStatus();
            }
        });
    }

    @Test
    public void should_response_with_custom_http_statuses() throws Exception {
        final int numberOfRequestsBeforeSuccessfulStatus = 5;
        final HttpResponseStatus expectedSuccessStatus = HttpResponseStatus.ACCEPTED;
        final HttpResponseStatus expectedFailureStatus = HttpResponseStatus.REQUEST_TIMEOUT;

        CountingResponseHandler handlerUnderTest = new CountingResponseHandler(numberOfRequestsBeforeSuccessfulStatus).
                withSuccessStatus(expectedSuccessStatus).
                withFailureStatus(expectedFailureStatus);

        server.response(handlerUnderTest);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                triggerRequestAndExpectFailureStatus(numberOfRequestsBeforeSuccessfulStatus, expectedFailureStatus);
                triggerRequestAndExpectSuccessStatus(expectedSuccessStatus);
            }
        });
    }

    private void triggerRequestAndExpectDefaultSuccessStatus() throws IOException {
        triggerRequestAndExpectSuccessStatus(CountingResponseHandler.successResponseStatus);
    }

    private void triggerRequestAndExpectSuccessStatus(HttpResponseStatus expectedSuccessStatus) throws IOException {
        int actualResponseStatus = Request.Post("http://localhost:" + RemoteTestUtils.port()).execute().returnResponse().getStatusLine().getStatusCode();
        assertThat(actualResponseStatus, is(expectedSuccessStatus.code()));
    }

    private void triggerRequestAndExpectDefaultFailureStatus(int numberOfRequestsBeforeSuccessfulStatus) throws IOException {
        triggerRequestAndExpectFailureStatus(numberOfRequestsBeforeSuccessfulStatus, CountingResponseHandler.failureResponseStatus);
    }

    private void triggerRequestAndExpectFailureStatus(int numberOfRequestsBeforeSuccessfulStatus, HttpResponseStatus httpResponseStatus) throws IOException {
        for (int i = 0; i < numberOfRequestsBeforeSuccessfulStatus; i++) {
            int actualResponseStatus = Request.Post("http://localhost:" + RemoteTestUtils.port()).execute().returnResponse().getStatusLine().getStatusCode();
            assertThat(actualResponseStatus, is(CountingResponseHandler.failureResponseStatus.code()));
        }
    }
}
