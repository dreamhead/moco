package com.github.dreamhead.moco.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StatusCodeResponseHandlerTest {

	private FullHttpRequest request;
	private FullHttpResponse response;
	private StatusCodeResponseHandler handler;
	private HttpResponseStatus status;

	@Before
	public void setUp() throws Exception {
		request = mock(FullHttpRequest.class);
		response = mock(FullHttpResponse.class);
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				status = (HttpResponseStatus) invocation.getArguments()[0];
				return null;
			}
		}).when(response).setStatus(any(HttpResponseStatus.class));
	}

	@Test
	public void should_return_not_found() {
		handler = new StatusCodeResponseHandler(404);
		handler.writeToResponse(request, response);
		assertThat(status.reasonPhrase(), is("Not Found"));
	}

	@Test
	public void should_return_unknown_status() {
		handler = new StatusCodeResponseHandler(601);
		handler.writeToResponse(request, response);
		assertThat(status.reasonPhrase(), is("Unknown Status (601)"));
	}

}
