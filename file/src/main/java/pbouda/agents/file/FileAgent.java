package pbouda.agents.file;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import pbouda.agents.core.AgentHelper;
import pbouda.agents.core.MapHolderUtils;
import pbouda.agents.core.UtilsInitializer;
import pbouda.agents.file.advice.FileCloseAdvice;
import pbouda.agents.file.advice.FileOpenAdvice;

import java.io.FileDescriptor;
import java.lang.instrument.Instrumentation;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class FileAgent {

    private static final String DATA_KEEPER_CLASS_NAME = "pbouda.agents.file.FileDescriptorHolder";

    public static void premain(String agentArgs, Instrumentation inst) {
        AgentHelper.execute(FileAgent.class, inst, FileAgent::transformation, () -> MapHolderUtils.reset(DATA_KEEPER_CLASS_NAME));
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        AgentHelper.execute(FileAgent.class, inst, FileAgent::transformation, () -> MapHolderUtils.reset(DATA_KEEPER_CLASS_NAME));
    }

    private static List<ResettableClassFileTransformer> transformation(Instrumentation inst) {
        ElementMatcher<? super MethodDescription> openMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("open")
                && methodDesc.getParameters().get(0).getType().asErasure().getName().equals(FileDescriptor.class.getName());

        ElementMatcher<? super MethodDescription> closeMatcher = methodDesc ->
                methodDesc.isMethod()
                && methodDesc.getActualName().equals("implCloseChannel");

        MapHolderUtils.initialize(DATA_KEEPER_CLASS_NAME, Integer.class, String.class);
        UtilsInitializer.initialize();

        ResettableClassFileTransformer transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(new AgentBuilder.Listener.WithErrorsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .with(RedefinitionStrategy.RETRANSFORMATION)
                // Allow matching classes from Platform Classloader
                .ignore(none())
                .type(named("sun.nio.ch.FileChannelImpl"))
                .transform((builder, typeDescription, classLoader, module) -> {
                    return builder
                            .visit(Advice.to(FileOpenAdvice.class).on(openMatcher))
                            .visit(Advice.to(FileCloseAdvice.class).on(closeMatcher));
                })
                .installOn(inst);

        return List.of(transformer);
    }
}