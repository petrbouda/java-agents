package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;

import java.net.InetAddress;
import java.time.Duration;

public class NioSocketCloseAdvice {

    private static final int UNKNOWN = -1;

    private static final String LOG = """
            {"logger": "socket-agent", "connection_id": %s, "elapsed": %s, "lifespan": %s, "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static long onEnter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(
            @Advice.This Object current,
            @Advice.FieldValue(value = "port") int port,
            @Advice.FieldValue(value = "address") InetAddress address,
            @Advice.Enter long time) {

        long now = System.nanoTime();
        long elapsed = Duration.ofNanos(now - time).toMillis();

        System.out.println("lifespan: " + SocketLifespanKeeper.lifespan);

        int connectionId = current.hashCode();
        Long timestamp = SocketLifespanKeeper.lifespan.remove(connectionId);
        Duration lifespan = timestamp != null
                ? Duration.ofNanos(now - timestamp)
                : Duration.ofMillis(UNKNOWN);

        String message = "Socket #close: address=" + address + ":" + port;
        String formatted = LOG.formatted(connectionId, elapsed, lifespan.toMillis(), Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
