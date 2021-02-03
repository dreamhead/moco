package com.github.dreamhead.moco.util;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class IterablesTest {
    @Test
    public void should_get_head() {
        assertThat(Iterables.head(new Integer[]{1, 2}), is(1));
        assertThat(Iterables.head(new Integer[]{1}), is(1));
        assertThat(Iterables.head(new Integer[0]), nullValue());
    }

    @Test
    public void should_get_tail() {
        assertThat(Iterables.tail(new Integer[]{1, 2}), is(new Integer[] {2}));
        assertThat(Iterables.tail(new Integer[1]), is(new Integer[0]));
        assertThat(Iterables.tail(new Integer[0]), is(new Integer[0]));
    }

    @Test
    public void should_be_null_or_empty() {
        assertThat(Iterables.isNullOrEmpty(null), is(true));
        assertThat(Iterables.isNullOrEmpty(Arrays.asList()), is(true));
        assertThat(Iterables.isNullOrEmpty(Arrays.asList(1)), is(false));
    }
}