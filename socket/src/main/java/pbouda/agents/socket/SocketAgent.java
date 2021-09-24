package pbouda.agents.socket;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import pbouda.agents.core.AgentHelper;
import pbouda.agents.core.MapHolderUtils;
import pbouda.agents.core.UtilsInitializer;
import pbouda.agents.socket.niosocketimpl.NioSocketImplTransformation;
import pbouda.agents.socket.socketchannelimpl.SocketChannelImplTransformation;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class SocketAgent {

    private static final List<TransformationFeature> FEATURES = List.of(
            new NioSocketImplTransformation(),
            new SocketChannelImplTransformation()
    );

    private static final String DATA_KEEPER_CLASS_NAME = "pbouda.agents.socket.SocketLifespanHolder";

    public static void premain(String agentArgs, Instrumentation inst) {
        AgentHelper.execute(SocketAgent.class, inst, SocketAgent::transformation, () -> MapHolderUtils.reset(DATA_KEEPER_CLASS_NAME));
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        AgentHelper.execute(SocketAgent.class, inst, SocketAgent::transformation, () -> MapHolderUtils.reset(DATA_KEEPER_CLASS_NAME));
    }

    private static List<ResettableClassFileTransformer> transformation(Instrumentation inst) {
        UtilsInitializer.initialize();
        MapHolderUtils.initialize(DATA_KEEPER_CLASS_NAME, Integer.class, Long.class);

        return FEATURES.stream()
                .flatMap(trans -> trans.apply(inst).stream())
                .toList();
    }
}