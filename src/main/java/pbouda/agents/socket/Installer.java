package pbouda.agents.socket;

import pbouda.agents.socket.action.AttachCommandLineAction;
import pbouda.agents.socket.action.CloseCommandLineAction;
import pbouda.agents.socket.action.CommandLineAction;

import java.util.List;
import java.util.Map;

public class Installer {

    public static final String CONTAINER_PID = "1";

    public static void main(String[] args) throws Exception {
        Map<Flag, String> flags = Flag.resolve(args);

        List<CommandLineAction> actions = List.of(
                new CloseCommandLineAction(),
                new AttachCommandLineAction());

        for (CommandLineAction action : actions) {
            if (action.applicable(flags)) {
                action.run(flags);
                return;
            }
        }
    }
}
