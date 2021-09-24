package pbouda.agents.socket;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class AbstractServerTest {

    static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        Instrumentation inst = ByteBuddyAgent.install();
        SocketAgent.premain(null, inst);
    }

    @AfterEach
    public void tearDown() {
        Instrumentation inst = ByteBuddyAgent.install();
        SocketAgent.premain(null, inst);
    }

    int startServer() {
        int port = findRandomFreePort();
        Runnable accept = () -> {
            try (ServerSocket serverSocket = new ServerSocket(port, 0, LOCALHOST)) {
                Socket in = serverSocket.accept();
                System.out.println("Connected: " + in.getLocalAddress() + ":" + in.getLocalPort());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread = new Thread(accept);
        thread.start();

        return port;
    }

    private static int findRandomFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find a free port", e);
        }
    }
}
