package pbouda.agents.socket.action;

import com.sun.tools.attach.VirtualMachine;
import pbouda.agents.socket.Flag;
import pbouda.agents.socket.Installer;
import pbouda.agents.socket.InterProcessMessageExchanger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class AttachCommandLineAction implements CommandLineAction {

    private static final Path CURRENT_DIR = Path.of(System.getProperty("user.dir"));
    private static final Path AGENT_PATH = CURRENT_DIR.resolve("http-agent.jar");
    private static final String AGENT_NAME = "HttpAgent";

    @Override
    public boolean applicable(Map<Flag, String> flags) {
        return flags.containsKey(Flag.ATTACH);
    }

    @Override
    public void run(Map<Flag, String> flags) throws Exception {
        String pid = flags.getOrDefault(Flag.PID, Installer.CONTAINER_PID);

        Path socketFilePath = InterProcessMessageExchanger.socketFilePath(Long.parseLong(pid));

        if (Files.exists(socketFilePath)) {
            System.out.println("[ERROR] Unix Socket file already exists (Agent can be already applied, close it first): " + socketFilePath);
        }

        String info = """
                Working Directory = %s
                Agent Path = %s
                Target PID = %s
                """.formatted(CURRENT_DIR, AGENT_PATH, pid);

        System.out.println(info);

        VirtualMachine vm = VirtualMachine.attach(pid);
        try {
            vm.loadAgent(AGENT_PATH.toString(), AGENT_NAME);
        } finally {
            vm.detach();
        }
    }
}
