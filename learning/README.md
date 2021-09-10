# Learning about Agents

```
# Run the empty-application
java -jar empty-application.jar  

# Run the empty-agent twice!
java -jar empty-agent.jar 94271
java -jar empty-agent.jar 94271

-------------------------------------
# the empty-application's output

Started - PID: 94271
Agent loaded! - 1
Agent loaded! - 2
```

- Even if start the agent twice, we have only 1 class of agent in `app` classloader

```
jcmd 94271 VM.classloaders show-classes                                                  [21/09/10| 1:08PM]
94271:
+-- <bootstrap>
      |     
      |               Classes: java.lang.Object
      |                        [Ljava.lang.Object;
      |                        [[Ljava.lang.Object;
      |                        java.io.Serializable
      |                        [Ljava.io.Serializable;
      |                        java.lang.Comparable
   ...
      |                        sun.instrument.TransformerManager
      |                        sun.instrument.TransformerManager$TransformerInfo
      |                        [Lsun.instrument.TransformerManager$TransformerInfo;
      |                        sun.instrument.InstrumentationImpl$1
      |                        jdk.internal.reflect.NativeMethodAccessorImpl
      |                        jdk.internal.reflect.DelegatingMethodAccessorImpl
      |                        (783 classes)
      |     
      |        Hidden Classes: java.lang.invoke.LambdaForm$MH/0x0000000800c05400
      |                        java.lang.invoke.LambdaForm$MH/0x0000000800c05000
                 ...
      |                        (19 hidden classes)
      |     
      +-- "platform", jdk.internal.loader.ClassLoaders$PlatformClassLoader
            |     
            +-- "app", jdk.internal.loader.ClassLoaders$AppClassLoader
                        
                                  Classes: pbouda.empty.application.Main
                                           pbouda.empty.agent.AgentMain
                                           (2 classes)
```