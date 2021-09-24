package pbouda.agents.socket;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.List;

public interface TransformationFeature {

    List<ResettableClassFileTransformer> apply(Instrumentation inst);

}
