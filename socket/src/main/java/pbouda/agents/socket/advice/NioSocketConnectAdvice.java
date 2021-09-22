package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;
import pbouda.agents.socket.SocketLifespanHolder;

import java.net.SocketAddress;
import java.time.Duration;

public class NioSocketConnectAdvice {

    private static final String LOG = """
            {"logger": "socket-agent", "connection_id": %s, "elapsed": %s, "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static long onEnter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(
            @Advice.Thrown Throwable throwable,
            @Advice.This Object current,
            @Advice.Argument(0) SocketAddress address,
            @Advice.Argument(1) int timeout,
            @Advice.Enter long time) {

        long now = System.nanoTime();

        int connectionId = current.hashCode();
        String message;
        if (throwable == null) {
            // NioSocketImpl uses the Default Identity Hashcode
            SocketLifespanHolder.data.put(connectionId, now);
            message = "Socket #connect: address=" + address + " timeout=" + timeout;
        } else {
            message = "Socket #connect: address=" + address
                      + " timeout=" + timeout
                      + " exception=" + throwable.getClass().getSimpleName()
                      + " error_message=" + throwable.getMessage();
        }

        long elapsed = Duration.ofNanos(now - time).toMillis();

        String formatted = LOG.formatted(connectionId, elapsed, Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
