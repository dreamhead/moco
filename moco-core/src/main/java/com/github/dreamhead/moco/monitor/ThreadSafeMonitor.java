package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.dreamhead.moco.util.Locks.withLock;

public final class ThreadSafeMonitor implements MocoMonitor {
    private final MocoMonitor monitor;
    private final Lock lock = new ReentrantLock();

    public ThreadSafeMonitor(final MocoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void onMessageArrived(final Request request) {
        withLock(lock, () -> monitor.onMessageArrived(request));
    }

    @Override
    public void onException(final Throwable t) {
        withLock(lock, () -> monitor.onException(t));
    }

    @Override
    public void onMessageLeave(final Response response) {
        withLock(lock, () -> monitor.onMessageLeave(response));
    }

    @Override
    public void onUnexpectedMessage(final Request request) {
        withLock(lock, () -> monitor.onUnexpectedMessage(request));
    }

    @Override
    public boolean isQuiet() {
        return monitor.isQuiet();
    }
}
