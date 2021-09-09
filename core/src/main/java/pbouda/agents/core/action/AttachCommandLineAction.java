package pbouda.agents.core.action;

import com.sun.tools.attach.VirtualMachine;
import pbouda.agents.core.Commander;
import pbouda.agents.core.Flag;
import pbouda.agents.core.messaging.InterProcessMessageExchanger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class AttachCommandLineAction implements CommandLineAction {

    private static final Path CURRENT_DIR = Path.of(System.getProperty("user.dir"));
    private static final Path AGENT_PATH = CURRENT_DIR.resolve("http-agent.jar");

    private final String agentName;

    public AttachCommandLineAction(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public boolean applicable(Map<Flag, String> flags) {
        return flags.containsKey(Flag.ATTACH);
    }

    @Override
    public void run(Map<Flag, String> flags) {
        String pid = flags.getOrDefault(Flag.PID, Commander.CONTAINER_PID);

        Path socketFilePath = InterProcessMessageExchanger.socketFilePath(agentName, Long.parseLong(pid));

        if (Files.exists(socketFilePath)) {
            String message = "[ERROR] Unix Socket file already exists (Agent can be already applied, close it first): " + socketFilePath;
            System.out.println(message);
            return;
        }

        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            try {
                vm.loadAgent(AGENT_PATH.toString());
            } finally {
                vm.detach();
            }
        } catch (Exception ex) {
            System.out.println("[ERROR] Cannot properly load the agent: " + ex.getMessage());
            return;
        }

        System.out.println("The agent successfully loaded: " + agentName);
    }
}
