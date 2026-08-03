package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;

import java.util.List;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AbstractResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

import static com.github.dreamhead.moco.Moco.group;

public class DynamicReplayHandler extends AbstractResponseHandler {
    private final RecorderRegistry registry;
    private final RecorderIdentifier identifier;
    private final ReplayModifier modifier;

    public DynamicReplayHandler(final RecorderConfigurations configurations) {
        this.registry = configurations.getRecorderRegistry();
        this.identifier = configurations.getIdentifier();
        this.modifier = configurations.getModifier();
    }

    private HttpRequest getRequiredRecordedRequest(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            throw new IllegalArgumentException("No recorded request for [" + identifier + "]");
        }
        return recordedRequest;
    }

    protected final HttpRequest getRecordedRequest(final HttpRequest request) {
        String name = this.identifier.getIdentifier(request);
        RequestRecorder recorder = registry.recorderOf(name);
        return recorder.getRequest();
    }

    @Override
    public final void writeToResponse(final SessionContext context) {
        Request request = context.getRequest();
        HttpRequest recordedRequest = getRequiredRecordedRequest((HttpRequest) request);
        SessionContext newContext = new SessionContext(recordedRequest, context.getResponse());
        modifier.writeToResponse(newContext);
    }

    protected final ResponseHandler doApply(final MocoConfig config) {
        RecorderIdentifier appliedIdentifier = this.identifier.apply(config);
        ReplayModifier appliedModifier = this.modifier.apply(config);

        if (appliedIdentifier != this.identifier || appliedModifier != this.modifier) {
            RecorderConfigurations configurations = RecorderConfigurations.create(List.of(
                    group(this.registry.getGroup()),
                    appliedIdentifier,
                    appliedModifier
            ));

            return new DynamicReplayHandler(configurations);
        }

        return this;
    }
}
