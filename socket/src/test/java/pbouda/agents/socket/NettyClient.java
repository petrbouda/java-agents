package pbouda.agents.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.AbstractNioByteChannel;

public final class NettyClient implements AutoCloseable {

    private final int port;
    private final EventLoopGroup loopGroup;
    private final Class<? extends AbstractNioByteChannel> socketChannelClass;
    private Channel channel;

    public NettyClient(
            int port,
            EventLoopGroup loopGroup,
            Class<? extends AbstractNioByteChannel> socketChannel) {

        this.port = port;
        this.loopGroup = loopGroup;
        this.socketChannelClass = socketChannel;
    }

    public Channel connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap()
                .group(loopGroup)
                .channel(socketChannelClass)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", port).sync()
                .addListener(f -> System.out.println("Client connected"))
                .syncUninterruptibly();

        channel = channelFuture.channel();

        return channelFuture.channel();
    }

    @Override
    public void close() throws InterruptedException {
        channel.close().await(500);
    }
}