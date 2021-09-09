package pbouda.agents.socket.advice;

import net.bytebuddy.asm.Advice;

import java.net.Socket;
import java.time.Duration;

public class SocketCloseAdvice {

    private static final String LOG = """
            {"logger": "http-agent", "elapsed": %s, "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static long onEnter() {
        return System.nanoTime();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(
            @Advice.This Socket current,
            @Advice.Enter long time) {

        long elapsed = Duration.ofNanos(System.nanoTime() - time).toMillis();
        String message = "Socket #close: address=" + current.getInetAddress();
        String formatted = LOG.formatted(elapsed, Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
