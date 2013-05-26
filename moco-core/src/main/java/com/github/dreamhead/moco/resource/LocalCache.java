package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public interface LocalCache {
    void write(byte[] content);
    byte[] read();

    void apply(MocoConfig config);

    LocalCache EMPTY_LOCAL_CACHE = new LocalCache() {
        @Override
        public void write(byte[] content) {
        }

        @Override
        public byte[] read() {
            return new byte[0];
        }

        @Override
        public void apply(MocoConfig config) {
        }
    };
}