package com.github.dreamhead.moco.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ReaderLineIterator implements Iterator<String> {
    private final BufferedReader reader;
    private String nextLine;

    public ReaderLineIterator(final Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    @Override
    public boolean hasNext() {
        if (nextLine != null) {
            return true;
        }
        try {
            nextLine = reader.readLine();
            return nextLine != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        String line = nextLine;
        nextLine = null;
        return line;
    }
}
