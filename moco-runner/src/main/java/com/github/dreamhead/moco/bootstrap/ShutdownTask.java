package com.github.dreamhead.moco.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import static com.github.dreamhead.moco.bootstrap.ShutdownArgs.parse;

public class ShutdownTask implements BootstrapTask {
    private static Logger logger = LoggerFactory.getLogger(ShutdownTask.class);

    private final int defaultShutdownPort;
    private final String defaultShutdownKey;

    public ShutdownTask(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    @Override
    public void run(String[] args) {
        ShutdownArgs shutdownArgs = parse(args);
        socketShutdown(shutdownArgs.getShutdownPort(defaultShutdownPort), defaultShutdownKey);
    }

    public void socketShutdown(int shutdownPort, String shutdownMocoKey) {
        try {
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), shutdownPort);
            socket.setSoLinger(false, 0);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write((shutdownMocoKey + "\r\n").getBytes());
            outputStream.flush();
            socket.close();
        } catch (ConnectException e) {
            System.err.println("fail to shutdown, please specify correct shutdown port.");
        } catch (IOException e) {
            logger.error("exception is thrown", e);
            throw new RuntimeException(e);
        }
    }
}
