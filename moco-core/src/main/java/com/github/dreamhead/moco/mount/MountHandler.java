package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AbstractContentResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class MountHandler extends AbstractContentResponseHandler {
    private final MountPathExtractor extractor;

    private final File dir;
    private final MountTo target;

    public MountHandler(final File dir, final MountTo target) {
        this.dir = dir;
        this.target = target;
        this.extractor = new MountPathExtractor(target);
    }

    @Override
    protected MessageContent responseContent(final Request request) {
        if (!HttpRequest.class.isInstance(request)) {
            throw new RuntimeException("Only HTTP request is allowed");
        }

        try {
            byte[] bytes = Files.toByteArray(targetFile((HttpRequest) request));
            return content().withContent(bytes).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File targetFile(HttpRequest request) {
        Optional<String> relativePath = extractor.extract(request);
        return new File(dir, relativePath.get());
    }

    @Override
    protected String getContentType(HttpRequest request) {
        return new FileContentType(targetFile(request).getName()).getContentType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return super.apply(config);
        }

        if (config.isFor(MocoConfig.URI_ID)) {
            return new MountHandler(this.dir, this.target.apply(config));
        }

        if (config.isFor(MocoConfig.FILE_ID)) {
            return new MountHandler(new File((String)config.apply(this.dir.getName())), this.target);
        }

        return this;
    }
}
