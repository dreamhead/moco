package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.google.common.base.Optional;

import java.io.InputStream;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Optional.of;

public class RunnerSetting {
    private InputStream stream;
    private final Optional<ResponseSetting> response;
    private final Optional<String> context;
    private final Optional<String> fileRoot;

    public RunnerSetting(InputStream stream, String context, String fileRoot, ResponseSetting response) {
        this.stream = stream;
        this.response = fromNullable(response);
        this.context = fromNullable(context);
        this.fileRoot = fromNullable(fileRoot);
    }

    public InputStream getStream() {
        return stream;
    }

    public Optional<MocoConfig> context() {
        if (context.isPresent()) {
            return of(Moco.context(context.get()));
        }

        return absent();
    }

    public Optional<MocoConfig> fileRoot() {
        if (fileRoot.isPresent()) {
            return of(Moco.fileRoot(fileRoot.get()));
        }

        return absent();
    }

    public Optional<MocoConfig> response() {
        if (response.isPresent()) {
            return of(Moco.response(response.get().getResponseHandler()));
        }

        return absent();
    }

}
