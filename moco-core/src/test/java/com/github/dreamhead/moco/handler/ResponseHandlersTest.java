package com.github.dreamhead.moco.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;

public class ResponseHandlersTest {

	private Resource resource;
	private ContentResource contentResource;

	@Before
	public void setUp() throws Exception {
		resource = mock(Resource.class);
		contentResource = mock(ContentResource.class);
	}

	@Test
	public void should_return_content_handle() {
		when(contentResource.id()).thenReturn("text");
		assertThat(ResponseHandlers.responseHandler(contentResource).getClass()
				.toString(), is(ContentHandler.class.toString()));
	}

	@Test
	public void should_return_version_handle() {
		when(resource.id()).thenReturn("version");
		assertThat(ResponseHandlers.responseHandler(resource).getClass()
				.toString(), is(VersionResponseHandler.class.toString()));
	}

	@Test(expected = RuntimeException.class)
	public void should_throw_exception_when_no_handle() throws Exception {
		when(resource.id()).thenReturn("UNKNOWN");
		ResponseHandlers.responseHandler(resource);
	}

}
