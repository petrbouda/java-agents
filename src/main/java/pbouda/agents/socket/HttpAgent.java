package pbouda.agents.socket;

import pbouda.agents.socket.advice.SocketCloseAdvice;
import pbouda.agents.socket.advice.SocketConnectAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;
import java.net.Socket;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class HttpAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        startAgent(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        startAgent(inst);
    }

    private static void startAgent(Instrumentation inst) {
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

        InterProcessMessageExchanger exchanger = new InterProcessMessageExchanger();

        List<MessageCommand> commands = List.of(
                new MessageCommand("", () -> System.out.println("Empty message received")),
                new MessageCommand("close", () -> {
                    transformer.reset(inst, RedefinitionStrategy.RETRANSFORMATION);
                    exchanger.close();
                    System.out.println("Resetting JavaAgent's transformations");
                })
        );

        exchanger.listen(commands);
    }
}