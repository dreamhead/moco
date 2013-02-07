package com.github.dreamhead.moco.resource;

public interface LocalCache {
    void write(byte[] content);
    byte[] read();

    LocalCache EMPTY_LOCAL_CACHE = new LocalCache() {
        @Override
        public void write(byte[] content) {
        }

        @Override
        public byte[] read() {
            return new byte[0];
        }
    };
}