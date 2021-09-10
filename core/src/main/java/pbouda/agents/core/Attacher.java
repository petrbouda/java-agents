package pbouda.agents.core;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

public class Attacher {

    public static final String CONTAINER_PID = "1";

    public static void main(String[] args) {
        Map<Flag, String> flags = Flag.resolve(args);

        String pid = flags.getOrDefault(Flag.PID, Attacher.CONTAINER_PID);
        ByteBuddyAgent.attach(agentPath(), pid);
    }

    public static File agentPath() {
        try {
            String path = Attacher.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            return new File(path);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot attach to a target JVM", e);
        }
    }
}
