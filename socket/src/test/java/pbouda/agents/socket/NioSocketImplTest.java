package pbouda.agents.socket;

import org.junit.jupiter.api.Test;
import pbouda.agents.coretest.AssertOutput;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.function.Predicate;

public class NioSocketImplTest extends AbstractServerTest {

    @Test
    public void connect() throws Exception {
        int port = startServer();

        Predicate<String> predicate = line -> line.contains("JDK NioSocketImpl#connect");
        try (AssertOutput assertion = new AssertOutput(predicate); Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(LOCALHOST, port));
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void close() throws Exception {
        int port = startServer();

        Predicate<String> predicate = line -> line.contains("JDK NioSocketImpl#close");
        try (AssertOutput assertion = new AssertOutput(predicate); Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(LOCALHOST, port));
            socket.close();
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }
}
