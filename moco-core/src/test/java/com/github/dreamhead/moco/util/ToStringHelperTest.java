package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ToStringHelperTest {
    @Test
    public void should_render_simple_name_and_values() {
        assertThat(ToStringHelper.of(this).add("foo", 1).add("bar", "two").toString(),
                is("ToStringHelperTest{foo=1, bar=two}"));
    }

    @Test
    public void should_render_no_values() {
        assertThat(ToStringHelper.of(this).toString(), is("ToStringHelperTest{}"));
    }

    @Test
    public void should_render_null_by_default() {
        assertThat(ToStringHelper.of(this).add("foo", null).toString(), is("ToStringHelperTest{foo=null}"));
    }

    @Test
    public void should_omit_null_values_when_asked() {
        assertThat(ToStringHelper.of(this).omitNullValues().add("foo", null).add("bar", 2).toString(),
                is("ToStringHelperTest{bar=2}"));
    }

    @Test
    public void should_render_arrays_by_content() {
        assertThat(ToStringHelper.of(this).add("foo", new String[]{"a", "b"}).toString(),
                is("ToStringHelperTest{foo=[a, b]}"));
    }
}
