package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.reader.FileResourceReader;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;
import com.google.common.net.MediaType;

import java.io.File;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.text;
import static com.google.common.base.Optional.of;

public class MountHandler extends AbstractHttpContentResponseHandler {
    private final MountPathExtractor extractor;

    private final File dir;
    private final MountTo target;

    public MountHandler(final File dir, final MountTo target) {
        this.dir = dir;
        this.target = target;
        this.extractor = new MountPathExtractor(target);
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        FileResourceReader reader = new FileResourceReader(asResource(httpRequest), Optional.<Charset>absent());
        return reader.readFor(of(httpRequest));
    }

    private Resource asResource(final HttpRequest httpRequest) {
        return text(targetFile(httpRequest).getPath());
    }

    private File targetFile(final HttpRequest request) {
        Optional<String> relativePath = extractor.extract(request);
        if (!relativePath.isPresent()) {
            throw new IllegalStateException("Reach mount handler without relative path");
        }

        return new File(dir, relativePath.get());
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
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
            return new MountHandler(new File((String) config.apply(this.dir.getName())), this.target);
        }

        return this;
    }
}
