package pbouda.agents.socket.action;

import pbouda.agents.socket.Flag;
import pbouda.agents.socket.Installer;
import pbouda.agents.socket.InterProcessMessageExchanger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CloseCommandLineAction implements CommandLineAction {

    @Override
    public boolean applicable(Map<Flag, String> flags) {
        return flags.containsKey(Flag.CLOSE);
    }

    @Override
    public void run(Map<Flag, String> flags) {
        long pid = Long.parseLong(flags.getOrDefault(Flag.PID, Installer.CONTAINER_PID));

        Path socketFilePath = InterProcessMessageExchanger.socketFilePath(pid);
        if (Files.exists(socketFilePath)) {
            var exchanger = new InterProcessMessageExchanger(pid);
            exchanger.write("close");
        } else {
            System.out.println("[ERROR] Socket file for the Java Agent does not exist: " + socketFilePath);
        }
    }
}
