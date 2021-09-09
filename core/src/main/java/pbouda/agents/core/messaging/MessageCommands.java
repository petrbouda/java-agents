package pbouda.agents.core.messaging;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.List;

public abstract class MessageCommands {

    static MessageCommand emptyMessage() {
        return new MessageCommand("", () -> System.out.println("Empty message received"));
    }

    static MessageCommand closeAgent(
            String agentName,
            InterProcessMessageExchanger exchanger,
            Instrumentation inst,
            List<ResettableClassFileTransformer> transformers) {

        return new MessageCommand("close", () -> {
            for (ResettableClassFileTransformer transformer : transformers) {
                transformer.reset(inst, AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            }
            exchanger.close();
            System.out.println("Resetting JavaAgent's transformations: " + agentName);
        });
    }
}
