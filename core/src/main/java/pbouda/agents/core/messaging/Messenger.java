package pbouda.agents.core.messaging;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.List;

public abstract class Messenger {

    public static void install(String agentName, Instrumentation inst, List<ResettableClassFileTransformer> transformers) {
        InterProcessMessageExchanger exchanger = new InterProcessMessageExchanger(agentName, ProcessHandle.current().pid());

        List<MessageCommand> commands = List.of(
                MessageCommands.emptyMessage(),
                MessageCommands.closeAgent(agentName, exchanger, inst, transformers)
        );

        exchanger.listen(commands);
    }
}
