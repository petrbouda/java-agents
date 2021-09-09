package pbouda.agents.core;

import pbouda.agents.core.action.AttachCommandLineAction;
import pbouda.agents.core.action.CloseCommandLineAction;
import pbouda.agents.core.action.CommandLineAction;

import java.util.List;
import java.util.Map;

public class Commander {

    public static final String CONTAINER_PID = "1";

    public static void execute(String agentName, String[] args) {
        Map<Flag, String> flags = Flag.resolve(args);

        List<CommandLineAction> actions = List.of(
                new CloseCommandLineAction(agentName),
                new AttachCommandLineAction(agentName));

        // Picks up the very first applicable command
        for (CommandLineAction action : actions) {
            if (action.applicable(flags)) {
                action.run(flags);
                return;
            }
        }
    }
}
