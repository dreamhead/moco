package com.github.dreamhead.moco.mount;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.util.FileContentType;
import com.github.dreamhead.moco.handler.AbstractContentResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;

public class MountHandler extends AbstractContentResponseHandler {
    private MountPathExtractor extractor;

    private final File dir;
    private final MountTo target;

    public MountHandler(File dir, MountTo target) {
        this.dir = dir;
        this.target = target;
        this.extractor = new MountPathExtractor(target);
    }

    @Override
    protected void writeContentResponse(HttpRequest request, ChannelBuffer buffer) {
        try {
            buffer.writeBytes(toByteArray(targetFile(request)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File targetFile(HttpRequest request) {
        String relativePath = extractor.extract(request);
        return new File(dir, relativePath);
    }

    @Override
    protected String getContentType(HttpRequest request) {
        return new FileContentType(targetFile(request).getName()).getContentType();
    }

    @Override
    public void apply(MocoConfig config) {
        if (config.isFor("uri")) {
            this.extractor = new MountPathExtractor(target.apply(config));
        }
    }
}
