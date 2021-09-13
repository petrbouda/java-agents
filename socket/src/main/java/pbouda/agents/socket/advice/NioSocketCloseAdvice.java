package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;

import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

        System.out.println("lifespan: " + lifespanMap);

        Duration lifespan;
        int connectionId = current.hashCode();
        if (lifespanMap != null) {
            Long timestamp = lifespanMap.remove(connectionId);
            if (timestamp != null) {
                lifespan = Duration.ofNanos(now - timestamp);
            } else {
                lifespan = Duration.ofMillis(UNKNOWN);
            }
        } else {
            lifespan = Duration.ofMillis(UNKNOWN);
        }

        String message = "Socket #close: address=" + address + ":" + port;
        String formatted = LOG.formatted(connectionId, elapsed, lifespan.toMillis(), Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
