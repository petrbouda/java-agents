package pbouda.agents.core;

import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This helper <b>is not thread-safe</b>. It expects that the client is called from `Attach Listener` thread.
 * Feel free to implement this simple behaviour in your own agent if you need to run it on a different thread.
 */
public abstract class AgentHelper {

    private static final Runnable EMPTY_CLEANUP = () -> {};

    private static final Map<Class<?>, List<ResettableClassFileTransformer>> TRANSFORMERS = new HashMap<>();

    private static Instrumentation instrumentation;

    public static void execute(
            Class<?> agentClazz,
            Instrumentation inst,
            Function<Instrumentation, List<ResettableClassFileTransformer>> transformFn) {

        execute(agentClazz, inst, transformFn, EMPTY_CLEANUP);
    }

    public static void execute(
            Class<?> agentClazz,
            Instrumentation inst,
            Function<Instrumentation, List<ResettableClassFileTransformer>> transformFn,
            Runnable cleanup) {

        if (instrumentation == null) {
            instrumentation = inst;
        }

        List<ResettableClassFileTransformer> transformers = TRANSFORMERS.get(agentClazz);
        if (transformers == null) {
            transformers = transformFn.apply(instrumentation);
            TRANSFORMERS.put(agentClazz, transformers);
            System.out.println("[INFO] Agent applied: " + agentClazz.getSimpleName());
        } else {
            transformers.forEach(t -> t.reset(instrumentation, RedefinitionStrategy.RETRANSFORMATION));
            TRANSFORMERS.remove(agentClazz);
            cleanup.run();
            System.out.println("[INFO] Agent reset: " + agentClazz.getSimpleName());
        }
    }
}
