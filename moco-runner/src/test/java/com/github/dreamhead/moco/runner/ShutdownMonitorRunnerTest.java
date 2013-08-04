package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.ShutdownTask;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ShutdownMonitorRunnerTest extends AbstractRunnerTest {

    private final String SHUTDOWN_MOCO_KEY = "_SHUTDOWN_MOCO_KEY";
    private final int SHUTDOWN_PORT = 9527;

    @Before
    public void setup() {
        RunnerFactory factory = new RunnerFactory(SHUTDOWN_PORT, SHUTDOWN_MOCO_KEY);
        runner = factory.createRunner(new StartArgs(port(), SHUTDOWN_PORT, "src/test/resources/foo.json", null, null));
        runner.run();
    }

    @Test(expected = HttpHostConnectException.class)
    public void should_shutdown_runner_by_socket() throws IOException {
        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        shutdownMoco(SHUTDOWN_PORT, SHUTDOWN_MOCO_KEY);
        waitChangeHappens();
        helper.get(root());
    }

    @Test(expected = ConnectException.class)
    public void should_stop_runner_directly() throws IOException {
        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        runner.stop();

        shutdownMoco(SHUTDOWN_PORT, SHUTDOWN_MOCO_KEY);
    }

    @Test(expected = HttpHostConnectException.class)
    public void should_stop_runner_via_shutdown_task() throws IOException {
        try {
            assertThat(helper.get(root()), is("foo"));
        } catch (HttpHostConnectException e) {
            fail(e.getMessage());
        }

        ShutdownTask task = new ShutdownTask(SHUTDOWN_PORT, SHUTDOWN_MOCO_KEY);
        task.run(new String[0]);

        waitChangeHappens();
        helper.get(root());
    }

    private void shutdownMoco(int shutdownPort, String shutdownMocoKey) throws IOException {
        Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), shutdownPort);
        socket.setSoLinger(false, 0);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write((shutdownMocoKey + "\r\n").getBytes());

        outputStream.flush();
        socket.close();
    }
}
