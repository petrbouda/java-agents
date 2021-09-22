package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;
import pbouda.agents.socket.SocketLifespanHolder;
import sun.nio.ch.Utils;

import java.io.FileDescriptor;
import java.net.SocketAddress;
import java.time.Duration;

public class NioSocketConnectAdvice {

    private static final String LOG = """
            {"logger": "socket-agent", "file_descriptor": %s, "elapsed": %s, "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static long onEnter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(
            @Advice.Thrown Throwable throwable,
            @Advice.FieldValue("fd") FileDescriptor fd,
            @Advice.Argument(0) SocketAddress address,
            @Advice.Argument(1) int timeout,
            @Advice.Enter long time) {

        long now = System.nanoTime();

        String message;
        int fdValue = Utils.fdVal(fd);
        if (throwable == null) {
            SocketLifespanHolder.data.put(fdValue, now);
            message = "Socket #connect: address=" + address + " timeout=" + timeout + " fd=" + fdValue;
        } else {
            message = "Socket #connect: address=" + address
                      + " timeout=" + timeout
                      + " exception=" + throwable.getClass().getSimpleName()
                      + " error_message=" + throwable.getMessage()
                      + " fd=" + fdValue;
        }

        long elapsed = Duration.ofNanos(now - time).toMillis();
        String formatted = LOG.formatted(fdValue, elapsed, Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
