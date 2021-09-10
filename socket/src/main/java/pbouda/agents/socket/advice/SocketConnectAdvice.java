package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;

import java.net.SocketAddress;
import java.time.Duration;

public class SocketConnectAdvice {

    private static final String LOG = """
            {"logger": "socket-agent", "elapsed": %s, "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static long onEnter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(
            @Advice.Argument(0) SocketAddress address,
            @Advice.Argument(1) int timeout,
            @Advice.Enter long time) {

        long elapsed = Duration.ofNanos(System.nanoTime() - time).toMillis();
        String message = "Socket #connect: address=" + address + " timeout=" + timeout;
        String formatted = LOG.formatted(elapsed, Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
