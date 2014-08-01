package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public class CompositeMonitor implements MocoMonitor {
    private final MocoMonitor[] monitors;

    public CompositeMonitor(MocoMonitor[] monitors) {
        this.monitors = monitors;
    }

    @Override
    public void onMessageArrived(Request request) {
        for (MocoMonitor monitor : monitors) {
            monitor.onMessageArrived(request);
        }
    }

    @Override
    public void onException(Exception e) {
        for (MocoMonitor monitor : monitors) {
            monitor.onException(e);
        }
    }

    @Override
    public void onMessageLeave(Response response) {
        for (MocoMonitor monitor : monitors) {
            monitor.onMessageLeave(response);
        }
    }

    @Override
    public void onUnexpectedMessage(Request request) {
        for (MocoMonitor monitor : monitors) {
            monitor.onUnexpectedMessage(request);
        }
    }
}
