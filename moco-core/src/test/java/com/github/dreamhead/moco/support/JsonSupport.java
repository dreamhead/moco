package com.github.dreamhead.moco.support;

import com.github.dreamhead.moco.util.Jsons;
import com.google.common.net.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonSupport {
    public static void assertEquals(final String expected, final HttpResponse response) {
        HttpEntity entity = response.getEntity();
        MediaType mediaType = MediaType.parse(entity.getContentType().getValue());
        assertThat(mediaType.type(), is("application"));
        assertThat(mediaType.subtype(), is("json"));

        try {
            JSONAssert.assertEquals(expected,
                    EntityUtils.toString(entity), JSONCompareMode.LENIENT);
        } catch (JSONException | IOException e) {
            throw new AssertionError("fail to parse entity to json");
        }
    }

    public static void assertEquals(final Object expected, final HttpResponse response) {
        assertEquals(Jsons.toJson(expected), response);
    }

    public static void assertEquals(final Object expected, final String actual) {
        try {
            JSONAssert.assertEquals(Jsons.toJson(expected), actual, JSONCompareMode.LENIENT);
        } catch (JSONException e) {
            throw new AssertionError("fail to parse entity to json");
        }
    }
}
