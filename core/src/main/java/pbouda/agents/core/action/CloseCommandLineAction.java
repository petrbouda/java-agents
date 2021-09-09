package pbouda.agents.core.action;

import pbouda.agents.core.Flag;
import pbouda.agents.core.Commander;
import pbouda.agents.core.messaging.InterProcessMessageExchanger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CloseCommandLineAction implements CommandLineAction {

    private final String agentName;

    public CloseCommandLineAction(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public boolean applicable(Map<Flag, String> flags) {
        return flags.containsKey(Flag.CLOSE);
    }

    @Override
    public void run(Map<Flag, String> flags) {
        long pid = Long.parseLong(flags.getOrDefault(Flag.PID, Commander.CONTAINER_PID));

        Path socketFilePath = InterProcessMessageExchanger.socketFilePath(agentName, pid);
        if (Files.exists(socketFilePath)) {
            var exchanger = new InterProcessMessageExchanger(agentName, pid);
            exchanger.write("close");
        } else {
            System.out.println("[ERROR] Socket file for the Java Agent does not exist: " + socketFilePath);
        }
    }
}
