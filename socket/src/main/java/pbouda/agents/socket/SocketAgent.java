package pbouda.agents.socket;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import pbouda.agents.socket.advice.SocketCloseAdvice;
import pbouda.agents.socket.advice.SocketConnectAdvice;

import java.lang.instrument.Instrumentation;
import java.net.Socket;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class SocketAgent {

    private static Instrumentation instrumentation;
    private static ResettableClassFileTransformer transformer;

    public static void premain(String agentArgs, Instrumentation inst) {
        _start(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        _start(inst);
    }

    private static void _start(Instrumentation inst) {
        if (instrumentation == null && transformer == null) {
            instrumentation = inst;
            transformer = transformation(inst);
            System.out.println("[INFO] Agent applied: " + SocketAgent.class.getSimpleName());
        } else {
            transformer.reset(instrumentation, RedefinitionStrategy.RETRANSFORMATION);
            transformer = null;
            instrumentation = null;
            System.out.println("[INFO] Agent reset: " + SocketAgent.class.getSimpleName());
        }
    }

    private static ResettableClassFileTransformer transformation(Instrumentation inst) {
        ElementMatcher<? super MethodDescription> connectMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("connect")
                && methodDesc.getParameters().size() == 2;

        ElementMatcher<? super MethodDescription> closeMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("close")
                && methodDesc.getParameters().size() == 0;

        ResettableClassFileTransformer transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RedefinitionStrategy.RETRANSFORMATION)
                // Allow matching classes from Platform Classloader
                .ignore(none())
                .type(named(Socket.class.getName()))
                .transform(((builder, typeDescription, classLoader, module) -> {
                    return builder
                            .visit(Advice.to(SocketConnectAdvice.class).on(connectMatcher))
                            .visit(Advice.to(SocketCloseAdvice.class).on(closeMatcher));
                }))
                .installOn(inst);

        return transformer;
    }
}