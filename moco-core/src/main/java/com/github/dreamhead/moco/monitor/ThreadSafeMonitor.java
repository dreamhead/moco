package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public class ThreadSafeMonitor implements MocoMonitor {
    private MocoMonitor monitor;

    public ThreadSafeMonitor(MocoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public synchronized void onMessageArrived(Request request) {
        this.monitor.onMessageArrived(request);

    }

    @Override
    public synchronized void onException(Throwable t) {
        this.monitor.onException(t);
    }

    @Override
    public void onMessageLeave(Response response) {
        this.monitor.onMessageLeave(response);

    }

    @Override
    public void onUnexpectedMessage(Request request) {
        this.monitor.onUnexpectedMessage(request);
    }
}
