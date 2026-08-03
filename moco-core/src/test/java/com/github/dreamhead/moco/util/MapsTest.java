package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Guards the two properties that {@link Map#copyOf} and {@link java.util.stream.Collectors#toMap}
 * would silently break: insertion order, which header and query dumps render, and the
 * duplicate-key exception type, which reaches {@code MocoMonitor.onException}.
 */
public class MapsTest {
    @Test
    public void should_preserve_insertion_order_when_copying() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("foo", "1");
        source.put("bar", "2");
        source.put("baz", "3");

        assertThat(List.copyOf(Maps.orderedCopyOf(source).keySet()), is(List.of("foo", "bar", "baz")));
    }

    @Test
    public void should_preserve_encounter_order_when_collecting() {
        Map<String, String> collected = Stream.of("foo", "bar", "baz")
                .collect(Maps.toOrderedMap(key -> key, key -> key.toUpperCase()));

        assertThat(List.copyOf(collected.keySet()), is(List.of("foo", "bar", "baz")));
    }

    @Test
    public void should_reject_modification() {
        assertThrows(UnsupportedOperationException.class, () -> Maps.orderedCopyOf(Map.of("foo", "1")).put("bar", "2"));
    }

    @Test
    public void should_raise_illegal_argument_for_duplicate_key() {
        assertThrows(IllegalArgumentException.class, () -> Stream.of("foo", "foo")
                .collect(Maps.toOrderedMap(key -> key, key -> key)));
    }
}
