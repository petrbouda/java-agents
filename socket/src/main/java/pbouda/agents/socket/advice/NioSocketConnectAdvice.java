package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class NioSocketConnectAdvice {

    private static final int UNKNOWN = -1;

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

        ConcurrentHashMap<Integer, Long> lifespanMap = null;
        try {
            /*
             * {@link SocketLifespanKeeperUtils#CLASS_NAME} SocketLifespanKeeperUtils class is not
             * the boot classloader.
             */
            Class<?> clazz = Class.forName("pbouda.agents.socket.LifespanKeeper");
            Field lifespanField = clazz.getField("lifespan");
            lifespanMap = (ConcurrentHashMap<Integer, Long>) lifespanField.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        int connectionId = current.hashCode();
        String message;
        if (throwable == null) {
            if (lifespanMap != null) {
                // NioSocketImpl uses the Default Identity Hashcode
                lifespanMap.put(connectionId, now);
            }
            message = "Socket #connect: address=" + address
                      + " timeout=" + timeout;
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
