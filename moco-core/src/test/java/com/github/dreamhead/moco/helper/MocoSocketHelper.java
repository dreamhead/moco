package com.github.dreamhead.moco.helper;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class MocoSocketHelper implements Closeable {
    private Socket socket;
    private SocketAddress address;
    private PrintStream os;
    private BufferedReader reader;

    public MocoSocketHelper(final String target, final int port) {
        socket = new Socket();
        address = new InetSocketAddress(target, port);
    }

    public void connect() {
        try {
            socket.connect(address);
            socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1));
            OutputStream outputStream = this.socket.getOutputStream();
            this.os = new PrintStream(outputStream);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(final String request) {
        os.print(request);
        os.flush();

        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(final String request, final int readCount) {
        os.print(request);
        os.flush();

        try {
            char[] buffer = new char[readCount];
            reader.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
