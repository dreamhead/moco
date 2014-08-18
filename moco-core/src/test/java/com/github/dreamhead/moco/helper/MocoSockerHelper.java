package com.github.dreamhead.moco.helper;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MocoSockerHelper implements Closeable {
    private Socket socket;
    private SocketAddress address;
    private PrintStream os;
    private BufferedReader reader;

    public MocoSockerHelper(String target, int port) {
        socket = new Socket();
        address = new InetSocketAddress(target, port);
    }

    public void connect() {
        try {
            socket.connect(address);
            OutputStream outputStream = this.socket.getOutputStream();
            this.os = new PrintStream(outputStream);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(String request) {
        os.print(request);
        os.flush();

        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
