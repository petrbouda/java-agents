package pbouda.agents.socket.niosocketimpl.advice;

import net.bytebuddy.asm.Advice;
import pbouda.agents.socket.SocketLifespanHolder;
import sun.nio.ch.Utils;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.time.Duration;

public class NioSocketImplCloseAdvice {

    private static final int UNKNOWN = -1;

    private static final String LOG = """
            {"logger": "socket-agent", "file_descriptor": %s, "lifespan": %s, "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static int onEnter(@Advice.FieldValue("fd") FileDescriptor fd) {
        return Utils.fdVal(fd);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(
            @Advice.FieldValue("port") int port,
            @Advice.FieldValue("address") InetAddress address,
            @Advice.Enter int fdValue) {

        long now = System.nanoTime();

        Long timestamp = SocketLifespanHolder.data.remove(fdValue);
        Duration lifespan = timestamp != null
                ? Duration.ofNanos(now - timestamp)
                : Duration.ofMillis(UNKNOWN);

        String message = "JDK NioSocketImpl#close: address=" + address + ":" + port + " fd=" + fdValue;
        String formatted = LOG.formatted(fdValue, lifespan.toMillis(), Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
