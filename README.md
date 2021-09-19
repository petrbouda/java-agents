# Java Agents

- https://www.youtube.com/watch?v=oflzFGONG08
- https://www.youtube.com/watch?v=OF3YFGZcQkg
- Byte-buddy Advice annotations: https://medium.com/@lnishada/introduction-to-byte-buddy-advice-annotations-48ac7dae6a94
- https://medium.com/@knownsec404team/getting-to-know-javaagent-getting-all-the-loaded-classes-of-the-target-process-d613f471012
- Instrumentation in depth: https://www.fatalerrors.org/a/in-depth-understanding-of-instrument.html

### Usages

- it works as a switcher between APPLIED and RESET transformations (the first invocation starts transformations, the second reset them) 

```
# Attach the agent to the running JVM instance (Automatically, it uses PID 1 (JVM in containers))
java -jar socket-agent.jar --pid <pid>
```

### OpenJDK versions

- it's set up for Java 17, and it counts on: https://openjdk.java.net/jeps/353 
- OpenJDK 17 (since OpenJDK 13): `java.net.Socket` uses by default `sun.nio.ch.NioSocketImpl` (to become non-blocking for Loom Project)
- OpenJDK 11: `java.net.Socket` uses by default `java.net.PlainSocketImp`