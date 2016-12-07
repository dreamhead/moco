package com.github.dreamhead.moco.mount;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MountToTest {

    @Test
    public void should_get_relative_path_from_uri() {
        MountTo to = new MountTo("/dir");
        assertThat(to.extract("/dir/filename").get(), is("filename"));
    }

    @Test
    public void should_return_null_if_uri_does_not_match() {
        MountTo to = new MountTo("/dir");
        assertThat(to.extract("/target/filename").isPresent(), is(false));
    }

    @Test
    public void should_return_null_if_no_relative_path_found() {
        MountTo to = new MountTo("/dir");
        assertThat(to.extract("/dir/").isPresent(), is(false));
    }
}
