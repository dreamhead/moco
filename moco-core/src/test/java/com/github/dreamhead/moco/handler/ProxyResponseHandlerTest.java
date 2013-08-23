package com.github.dreamhead.moco.handler;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.dreamhead.moco.handler.failover.Failover;

public class ProxyResponseHandlerTest {

	private Method method;
	private URL url;
	private ProxyResponseHandler handler;
	private HttpRequest request;

	@Before
	public void setUp() throws Exception {
		url = new URL("http://github.io/moco/");
		handler = new ProxyResponseHandler(url, Mockito.mock(Failover.class));
	}

	@Test
	public void should_create_get_base_request() throws Exception {
		assertThat(
				createBaseRequest(url, HttpMethod.GET).getClass().toString(),
				is(HttpGet.class.toString()));
	}

	@Test(expected = InvocationTargetException.class)
	public void should_throw_exception_with_unknown_method() throws Exception {
		createBaseRequest(url, HttpMethod.valueOf("UNKNOWN_METHOD"));
	}

	@Test
	public void should_parse_http_1_0() throws Exception {
		request = new DefaultFullHttpRequest(
				io.netty.handler.codec.http.HttpVersion.HTTP_1_0,
				HttpMethod.GET, url.toString());
		HttpVersion version = createVersion(request);
		assertThat(version.getMajor(), is(1));
		assertThat(version.getMinor(), is(0));
	}

	@Test
	public void should_parse_http_1_1() throws Exception {
		request = new DefaultFullHttpRequest(
				io.netty.handler.codec.http.HttpVersion.HTTP_1_1,
				HttpMethod.GET, url.toString());
		HttpVersion version = createVersion(request);
		assertThat(version.getMajor(), is(1));
		assertThat(version.getMinor(), is(1));
	}

	@Test
	public void should_return_right_remote_url() throws Exception {
		request = new DefaultFullHttpRequest(
				io.netty.handler.codec.http.HttpVersion.HTTP_1_0,
				HttpMethod.GET, url.toString());
		URL url = remoteUrl(request);
		assertThat(url.getHost(), is("github.io"));
		assertThat(url.getProtocol(), is("http"));
		assertThat(url.getPort(), is(-1));
	}

	private HttpRequestBase createBaseRequest(URL url, HttpMethod httpMethod)
			throws Exception {
		method = ProxyResponseHandler.class.getDeclaredMethod(
				"createBaseRequest", URL.class, HttpMethod.class);
		method.setAccessible(true);
		return (HttpRequestBase) method.invoke(handler, url, httpMethod);
	}

	private HttpVersion createVersion(HttpRequest request) throws Exception {
		method = ProxyResponseHandler.class.getDeclaredMethod("createVersion",
				HttpRequest.class);
		method.setAccessible(true);
		return (HttpVersion) method.invoke(handler, request);
	}

	private URL remoteUrl(HttpRequest request) throws Exception {
		method = ProxyResponseHandler.class.getDeclaredMethod("remoteUrl",
				HttpRequest.class);
		method.setAccessible(true);
		return (URL) method.invoke(handler, request);
	}
}
