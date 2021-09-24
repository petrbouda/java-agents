package pbouda.agents.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.Test;
import pbouda.agents.coretest.AssertOutput;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

public class SocketChannelImplTest extends AbstractServerTest {

    private static final Function<Integer, NettyClient> CLIENT_SUPPLIER = port ->
            new NettyClient(port, new NioEventLoopGroup(), NioSocketChannel.class);

    @Test
    public void connect() throws InterruptedException {
        int port = startServer();

        Predicate<String> predicate = line -> line.contains("JDK SocketChannelImpl#connect");
        try (AssertOutput assertion = new AssertOutput(predicate); NettyClient client = CLIENT_SUPPLIER.apply(port)) {
            client.connect();
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void close() throws InterruptedException {
        int port = startServer();

        Predicate<String> predicate = line -> line.contains("JDK SocketChannelImpl#close");
        try (AssertOutput assertion = new AssertOutput(predicate); NettyClient client = CLIENT_SUPPLIER.apply(port)) {
            Channel channel = client.connect();
            channel.close().await(500);
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }
}
