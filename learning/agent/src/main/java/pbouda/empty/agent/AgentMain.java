package pbouda.empty.agent;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentMain {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static void premain(String agentArgs, Instrumentation inst) {
        startAgent(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        startAgent(agentArgs, inst);
    }

    private static void startAgent(String agentArgs, Instrumentation inst) {
        System.out.println("Agent loaded! - " + COUNTER.incrementAndGet());
    }
}
