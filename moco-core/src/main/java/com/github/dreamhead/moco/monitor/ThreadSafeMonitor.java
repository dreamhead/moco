package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.dreamhead.moco.util.Locks.withLock;

public final class ThreadSafeMonitor implements MocoMonitor {
    private MocoMonitor monitor;
    private Lock lock = new ReentrantLock();

    public ThreadSafeMonitor(final MocoMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void onMessageArrived(final Request request) {
        withLock(lock, new Runnable() {
            @Override
            public void run() {
                monitor.onMessageArrived(request);
            }
        });
    }

    @Override
    public void onException(final Throwable t) {
        withLock(lock, new Runnable() {
            @Override
            public void run() {
                monitor.onException(t);
            }
        });
    }

    @Override
    public void onMessageLeave(final Response response) {
        withLock(lock, new Runnable() {
            @Override
            public void run() {
                monitor.onMessageLeave(response);
            }
        });
    }

    @Override
    public void onUnexpectedMessage(final Request request) {
        withLock(lock, new Runnable() {
            @Override
            public void run() {
                monitor.onUnexpectedMessage(request);
            }
        });
    }
}
