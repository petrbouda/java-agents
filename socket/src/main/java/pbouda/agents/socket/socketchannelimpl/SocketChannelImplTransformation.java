package pbouda.agents.socket.socketchannelimpl;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import pbouda.agents.socket.TransformationFeature;
import pbouda.agents.socket.socketchannelimpl.advice.SocketChannelImplCloseAdvice;
import pbouda.agents.socket.socketchannelimpl.advice.SocketChannelImplConnectAdvice;

import java.lang.instrument.Instrumentation;
import java.net.SocketAddress;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class SocketChannelImplTransformation implements TransformationFeature {

    @Override
    public List<ResettableClassFileTransformer> apply(Instrumentation inst) {
        ElementMatcher<? super MethodDescription> connectMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("connect")
                && methodDesc.getParameters().get(0).getType().asErasure().getName().equals(SocketAddress.class.getName());

        ElementMatcher<? super MethodDescription> closeMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("implCloseSelectableChannel")
                && methodDesc.getParameters().size() == 0;

        ResettableClassFileTransformer transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(new AgentBuilder.Listener.WithErrorsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                // Allow matching classes from Platform/Boostrap Classloaders
                .ignore(none())
                .type(named("sun.nio.ch.SocketChannelImpl"))
                .transform((builder, typeDescription, classLoader, module) -> {
                    return builder
                            .visit(Advice.to(SocketChannelImplConnectAdvice.class).on(connectMatcher))
                            .visit(Advice.to(SocketChannelImplCloseAdvice.class).on(closeMatcher));
                })
                .installOn(inst);

        return List.of(transformer);
    }
}
