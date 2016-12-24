package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Runnable;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.socketServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ActualSocketServerTest {
    private MocoSocketHelper helper;

    @Before
    public void setUp() throws Exception {
        this.helper = new MocoSocketHelper(local(), port());
    }

    @Test
    public void should_merge_socket_servers() throws Exception {
        SocketServer server = socketServer(12306);
        SocketServer secondServer = socketServer(12306);

        server.request(by("foo")).response(line("bar"));
        secondServer.request(by("foo1")).response(line("bar1"));

        SocketServer newServer = ((ActualSocketServer) server).mergeServer((ActualSocketServer) secondServer);

        running(newServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                assertThat(helper.send("foo"), is("bar"));
                assertThat(helper.send("foo1"), is("bar1"));
                helper.close();
            }
        });
    }

    @Test
    public void should_merge_socket_servers_with_first_port() throws Exception {
        SocketServer server = socketServer(12306);
        SocketServer secondServer = socketServer();


        final SocketServer newServer = ((ActualSocketServer) server).mergeServer((ActualSocketServer) secondServer);

        running(newServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(newServer.port(), is(12306));
            }
        });
    }

    @Test
    public void should_merge_socket_servers_with_second_port() throws Exception {
        SocketServer server = socketServer();
        SocketServer secondServer = socketServer(12307);

        final SocketServer newServer = ((ActualSocketServer) server).mergeServer((ActualSocketServer) secondServer);

        running(newServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(newServer.port(), is(12307));
            }
        });
    }

    @Test
    public void should_merge_socket_servers_without_ports_for_both_server() throws Exception {
        SocketServer server = socketServer();
        SocketServer secondServer = socketServer();

        final ActualSocketServer newServer = ((ActualSocketServer) server).mergeServer((ActualSocketServer) secondServer);
        assertThat(newServer.getPort().isPresent(), is(false));
    }

    private String line(final String text) {
        return text + "\r\n";
    }
}
