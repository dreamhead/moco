package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.MatcherAssert.assertThat;

public class AntPathMatcherTest {
    @Test
    public void should_match_path_with_default_matcher() {
        AntPathMatcher matcher = AntPathMatcher.create();
        assertThat(matcher.match("/user/1", "/user/1"), is(true));
        assertThat(matcher.match("/user/1", "/user/2"), is(false));
    }

    @Test
    public void should_be_case_sensitive_by_default() {
        AntPathMatcher matcher = AntPathMatcher.create();
        assertThat(matcher.match("/user/1", "/user/1"), is(true));
        assertThat(matcher.match("/user/1", "/USER/1"), is(false));
    }

    @Test
    public void should_ignore_case_when_configured() {
        AntPathMatcher matcher = AntPathMatcher.ignoreCase().create();
        assertThat(matcher.match("/user/1", "/USER/1"), is(true));
        assertThat(matcher.match("/USER/*", "/user/1"), is(true));
        assertThat(matcher.match("/USER/*", "/other/1"), is(false));
    }

    @Test
    public void should_not_trim_tokens_by_default() {
        AntPathMatcher matcher = AntPathMatcher.create();
        assertThat(matcher.match("/user/1", "/user/1"), is(true));
        assertThat(matcher.match("/user/1", "/user /1"), is(false));
    }

    @Test
    public void should_trim_tokens_when_configured() {
        AntPathMatcher matcher = AntPathMatcher.trimTokens().create();
        assertThat(matcher.match("/user/*", "/user /1"), is(true));
        assertThat(matcher.match("/user/*", "/other /1"), is(false));
    }

    @Test
    public void should_use_custom_path_separator() {
        AntPathMatcher matcher = AntPathMatcher.pathSeparator('.').create();
        assertThat(matcher.match("path.*", "path.file"), is(true));
        assertThat(matcher.match("path.**", "path.a.b"), is(true));
        assertThat(matcher.match("path.*", "path.a.b"), is(false));
    }

    @Test
    public void should_match_single_star_wildcard() {
        AntPathMatcher matcher = AntPathMatcher.create();
        assertThat(matcher.match("/path/*", "/path/file"), is(true));
        assertThat(matcher.match("/path/*", "/path/sub/file"), is(false));
    }

    @Test
    public void should_match_question_mark_wildcard() {
        AntPathMatcher matcher = AntPathMatcher.create();
        assertThat(matcher.match("/path/f?le", "/path/file"), is(true));
        assertThat(matcher.match("/path/f?le", "/path/fee"), is(false));
    }

    @Test
    public void should_match_double_star_wildcard() {
        AntPathMatcher matcher = AntPathMatcher.create();
        assertThat(matcher.match("/path/**", "/path/a/b/c"), is(true));
        assertThat(matcher.match("/**/example", "/example"), is(true));
        assertThat(matcher.match("/path/**", "/other/a"), is(false));
    }

    @Test
    public void should_extract_uri_template_variables() {
        AntPathMatcher matcher = AntPathMatcher.create();
        Map<String, String> variables = matcher.extractUriTemplateVariables("/user/{id}", "/user/42");
        assertThat(variables, hasEntry("id", "42"));

        Map<String, String> noVariables = matcher.extractUriTemplateVariables("/user/1", "/user/1");
        assertThat(noVariables.isEmpty(), is(true));
    }
}
