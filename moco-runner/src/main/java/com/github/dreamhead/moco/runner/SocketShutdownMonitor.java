package com.github.dreamhead.moco.runner;

import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketShutdownMonitor {
    public static final int DEFAULT_STOP_INTERVAL = 1000;
    private static Logger logger = LoggerFactory.getLogger(SocketShutdownMonitor.class);

    private final String shutdownKey;
    private final ShutdownListener shutdownListener;
    private final ServerSocket serverSocket;

    private Thread thread;
    private boolean running = false;

    public SocketShutdownMonitor(int shutdownPort, String shutdownKey, ShutdownListener shutdownListener) {
        this.shutdownKey = shutdownKey;
        this.shutdownListener = shutdownListener;

        try {
            serverSocket = new ServerSocket(shutdownPort, 1, InetAddress.getByName("127.0.0.1"));
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void startMonitor() {
        thread = createMonitorThread();
        thread.start();
        this.running = true;
    }

    private Thread createMonitorThread() {
        Thread thread = new Thread(createShutdownSocketRunnable());
        thread.setDaemon(true);
        thread.setName("MocoShutdownMonitor");
        return thread;
    }

    public synchronized void stopMonitor() {
        if (thread != null && running) {
            running = false;
            try {
                serverSocket.close();
                thread.join(DEFAULT_STOP_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Runnable createShutdownSocketRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                while (serverSocket != null && running) {
                    try {
                        if (tryToShutdown()) {
                            break;
                        }
                    } catch (IOException e) {
                        logger.error("exception is thrown", e);
                        break;
                    }
                }
            }
        };
    }

    private boolean tryToShutdown() throws IOException {
        final Socket socket = serverSocket.accept();
        socket.setSoLinger(false, 0);

        if (!isShutdownSuccessfully(socket)) {
            return false;
        }

        socket.close();
        serverSocket.close();

        shutdownListener.onShutdown();
        return true;
    }

    private InputSupplier<InputStreamReader> toInputSupplier(final Socket socket) {
        return new InputSupplier<InputStreamReader>() {
            @Override
            public InputStreamReader getInput() throws IOException {
                return new InputStreamReader(socket.getInputStream());
            }
        };
    }

    private boolean isShutdownSuccessfully(Socket socket) throws IOException {
        String line = CharStreams.readFirstLine(toInputSupplier(socket));
        return shutdownKey.equals(line);
    }
}
