package com.github.dreamhead.moco.sse;

import com.github.dreamhead.moco.util.Preconditions;
import com.github.dreamhead.moco.util.Strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

public final class SseEventParser {
    public Iterable<SseEvent> parse(final Iterable<String> lines) {
        return new SseEventIterable(lines);
    }

    private final class SseEventIterable implements Iterable<SseEvent> {
        private final Iterable<String> lines;

        SseEventIterable(final Iterable<String> lines) {
            this.lines = lines;
        }

        @Override
        public Iterator<SseEvent> iterator() {
            return new SseEventIterator(lines.iterator());
        }
    }

    private final class SseEventIterator implements Iterator<SseEvent> {
        private final Iterator<String> lineIterator;
        private SseEvent next;

        SseEventIterator(final Iterator<String> lineIterator) {
            this.lineIterator = lineIterator;
        }

        @Override
        public boolean hasNext() {
            if (next != null) {
                return true;
            }
            next = readNext();
            return next != null;
        }

        @Override
        public SseEvent next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            SseEvent result = next;
            next = null;
            return result;
        }

        private SseEvent readNext() {
            List<String> blockLines = new ArrayList<>();
            while (lineIterator.hasNext()) {
                String line = lineIterator.next();
                if (Strings.strip(line).isEmpty()) {
                    if (!blockLines.isEmpty()) {
                        return parseBlock(blockLines);
                    }
                } else {
                    blockLines.add(line);
                }
            }
            if (!blockLines.isEmpty()) {
                return parseBlock(blockLines);
            }
            return null;
        }
    }

    private SseEvent parseBlock(final List<String> lines) {
        List<Map.Entry<String, String>> fields = parseFields(lines);
        Preconditions.checkArgument(hasData(fields),
                "SSE event must have at least one data field");
        return parseEvent(fields);
    }

    private List<Map.Entry<String, String>> parseFields(final List<String> lines) {
        List<Map.Entry<String, String>> fields = new ArrayList<>();
        for (String line : lines) {
            Map.Entry<String, String> field = parseLine(line);
            if (field != null) {
                fields.add(field);
            }
        }
        return List.copyOf(fields);
    }

    private Map.Entry<String, String> parseLine(final String line) {
        if (line.isEmpty()) {
            return null;
        }

        String[] parts = line.split(":", 2);
        if (parts.length < 2) {
            return null;
        }

        return Map.entry(Strings.strip(parts[0]), Strings.strip(parts[1]));
    }

    private boolean hasData(final List<Map.Entry<String, String>> fields) {
        return fields.stream().anyMatch(field -> "data".equals(field.getKey()));
    }

    private SseEvent parseEvent(final List<Map.Entry<String, String>> fields) {
        String id = null;
        String eventName = null;
        List<String> data = new ArrayList<>();
        Integer retry = null;

        for (Map.Entry<String, String> field : fields) {
            switch (field.getKey()) {
                case "id" -> id = field.getValue();
                case "event" -> eventName = field.getValue();
                case "data" -> data.add(field.getValue());
                case "retry" -> retry = Integer.parseInt(field.getValue());
                default -> { }
            }
        }

        return buildSseEvent(eventName, data, id, retry);
    }

    private SseEvent buildSseEvent(final String eventName, final List<String> data,
                                    final String id, final Integer retry) {
        return new SseEvent(id, eventName, data, retry, 0);
    }
}
