package pbouda.agents.socket;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.*;
import java.time.Duration;
import java.util.function.Predicate;

public class SocketTest {

    private static final InetAddress LOCALHOST;
    private static final int PORT = findRandomFreePort();

    static {
        try {
            LOCALHOST = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void connect() throws Exception {
        Instrumentation inst = ByteBuddyAgent.install();
        HttpAgent.premain(null, inst);

        startServer();

        Predicate<String> predicate = line -> line.contains("Socket #connect");
        try (AssertOutput assertion = new AssertOutput(predicate); Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(LOCALHOST, PORT));
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void close() throws Exception {
        Instrumentation inst = ByteBuddyAgent.install();
        HttpAgent.premain(null, inst);

        startServer();

        Predicate<String> predicate = line -> line.contains("Socket #close");
        try (AssertOutput assertion = new AssertOutput(predicate); Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(LOCALHOST, PORT));
            socket.close();
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    private static void startServer() {
        Runnable accept = () -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT, 0, LOCALHOST)) {
                Socket in = serverSocket.accept();
                System.out.println("Connected: " + in.getLocalAddress() + ":" + in.getLocalPort());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread = new Thread(accept);
        thread.start();
    }

    private static int findRandomFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find a free port", e);
        }
    }
}
