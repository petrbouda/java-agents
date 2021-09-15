package pbouda.agents.socket;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import pbouda.agents.core.AgentHelper;
import pbouda.agents.socket.advice.NioSocketCloseAdvice;
import pbouda.agents.socket.advice.NioSocketConnectAdvice;
import pbouda.agents.socket.advice.SocketLifespanKeeperUtils;

import java.lang.instrument.Instrumentation;
import java.net.SocketAddress;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class SocketAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        AgentHelper.execute(SocketAgent.class, inst, SocketAgent::transformation, SocketLifespanKeeperUtils::cleanup);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        AgentHelper.execute(SocketAgent.class, inst, SocketAgent::transformation, SocketLifespanKeeperUtils::cleanup);
    }

    private static List<ResettableClassFileTransformer> transformation(Instrumentation inst) {
        ElementMatcher<? super MethodDescription> connectMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("connect")
                && methodDesc.getParameters().get(0).getType().asErasure().getName().equals(SocketAddress.class.getName());

        ElementMatcher<? super MethodDescription> closeMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("close")
                && methodDesc.getParameters().size() == 0;

        SocketLifespanKeeperUtils.initialize();

        ResettableClassFileTransformer transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(new AgentBuilder.Listener.WithErrorsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .with(RedefinitionStrategy.RETRANSFORMATION)
                // Allow matching classes from Platform Classloader
                .ignore(none())
                .type(named("sun.nio.ch.NioSocketImpl"))
                .transform((builder, typeDescription, classLoader, module) -> {
                    return builder
                            .visit(Advice.to(NioSocketConnectAdvice.class).on(connectMatcher))
                            .visit(Advice.to(NioSocketCloseAdvice.class).on(closeMatcher));
                })
                .installOn(inst);

        return List.of(transformer);
    }
}