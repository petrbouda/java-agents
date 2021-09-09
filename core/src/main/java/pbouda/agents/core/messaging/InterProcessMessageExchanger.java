package pbouda.agents.core.messaging;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InterProcessMessageExchanger implements AutoCloseable {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(
            new NamedThreadFactory("agent-listener"));

    private static final String SOCKET_FILE_TEMPLATE = "%s-%s.socket";

    private final Path socketFile;

    private volatile ServerSocketChannel serverChannel;

    public InterProcessMessageExchanger(String agentName, long pid) {
        this(socketFilePath(agentName, pid));
    }

    public InterProcessMessageExchanger(Path socketFile) {
        this.socketFile = socketFile;
    }

    public void listen(List<MessageCommand> commands) {
        Runnable listener = () -> {
            System.out.println("Start HTTP-Agent Command Listener");

            try (ServerSocketChannel serverChannel = initUnixSocketServer(socketFile)) {
                this.serverChannel = serverChannel;

                while (true) {
                    // Read only a single message from every connected client
                    try (SocketChannel channel = serverChannel.accept()) {
                        Optional<String> incoming = readMessage(channel);

                        for (MessageCommand command : commands) {
                            if (command.message().equals(incoming.orElse(""))) {
                                command.action().run();
                            }
                        }
                    }
                }
            } catch (AsynchronousCloseException e) {
                System.out.println("MessageExchanger closed! No longer accepts any other commands.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };

        EXECUTOR.execute(listener);
    }

    public void write(String message) {
        try (SocketChannel channel = initUnixSocketClient()) {
            channel.connect(UnixDomainSocketAddress.of(socketFile));
            writeMessage(channel, message);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during writing a message: message=" + message, e);
        }
    }

    public static Path socketFilePath(String agentName, long pid) {
        return Path.of(System.getProperty("java.io.tmpdir"))
                .resolve(SOCKET_FILE_TEMPLATE.formatted(agentName, pid));
    }

    private static ServerSocketChannel initUnixSocketServer(Path socketFile) throws IOException {
        UnixDomainSocketAddress address =
                UnixDomainSocketAddress.of(socketFile);

        ServerSocketChannel channel = ServerSocketChannel
                .open(StandardProtocolFamily.UNIX);

        channel.configureBlocking(true);

        return channel.bind(address);
    }

    private static SocketChannel initUnixSocketClient() throws IOException {
        SocketChannel channel = SocketChannel
                .open(StandardProtocolFamily.UNIX);

        channel.configureBlocking(true);
        return channel;
    }

    private static Optional<String> readMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);
        if (bytesRead < 0) {
            return Optional.empty();
        }

        byte[] bytes = new byte[bytesRead];
        buffer.flip();
        buffer.get(bytes);
        String message = new String(bytes);
        return Optional.of(message);
    }

    private static void writeMessage(SocketChannel socketChannel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }

    @Override
    public void close() {
        if (serverChannel != null) {
            try {
                serverChannel.close();
                Files.deleteIfExists(socketFile);
            } catch (IOException e) {
                throw new RuntimeException("Cannot close Unix Socket for MessageExchanger", e);
            }
        }
    }
}
