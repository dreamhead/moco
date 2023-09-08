package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.tasks.ShutdownTask;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import static com.github.dreamhead.moco.bootstrap.arg.HttpArgs.httpArgs;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ShutdownWatcherRunnerTest extends AbstractRunnerTest {
    private static final String SHUTDOWN_MOCO_KEY = "_SHUTDOWN_MOCO_KEY";
    private static final int SHUTDOWN_PORT = 9527;

    @BeforeEach
    public void setup() {
        RunnerFactory factory = new RunnerFactory(SHUTDOWN_MOCO_KEY);
        runner = factory.createRunner(httpArgs()
                .withPort(port())
                .withShutdownPort(SHUTDOWN_PORT)
                .withConfigurationFile("src/test/resources/foo.json")
                .build());
    }

    @Test
    public void should_shutdown_runner_by_socket() throws IOException {
        runner.run();

        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        shutdownMoco(SHUTDOWN_PORT, SHUTDOWN_MOCO_KEY);
        waitChangeHappens();

        assertThrows(HttpHostConnectException.class, () -> helper.get(root()));
    }

    @Test
    public void should_stop_runner_directly() throws IOException {
        runner.run();

        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        runner.stop();

        assertThrows(ConnectException.class, () ->
                shutdownMoco(SHUTDOWN_PORT, SHUTDOWN_MOCO_KEY));
    }

    @Test
    public void should_stop_runner_via_shutdown_task() throws IOException {
        runner.run();

        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        ShutdownTask task = new ShutdownTask(SHUTDOWN_MOCO_KEY);
        task.run(new String[]{"-s", Integer.toString(SHUTDOWN_PORT)});

        waitChangeHappens();

        assertThrows(HttpHostConnectException.class, () -> helper.get(root()));
    }

    private void shutdownMoco(final int shutdownPort, final String shutdownMocoKey) throws IOException {
        Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), shutdownPort);
        socket.setSoLinger(false, 0);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write((shutdownMocoKey + "\r\n").getBytes());

        outputStream.flush();
        socket.close();
    }

    @Test
    public void should_shutdown_with_shutdown_port() throws IOException {
        RunnerFactory factory = new RunnerFactory(SHUTDOWN_MOCO_KEY);
        runner = factory.createRunner(httpArgs().withPort(port()).withConfigurationFile("src/test/resources/foo.json").build());
        runner.run();

        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        ShutdownRunner shutdownRunner = (ShutdownRunner) runner;
        int port = shutdownRunner.shutdownPort();
        shutdownMoco(port, SHUTDOWN_MOCO_KEY);
        waitChangeHappens();

        assertThrows(HttpHostConnectException.class, () -> helper.get(root()));
    }
}
