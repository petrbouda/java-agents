package pbouda.agents.core.action;

import pbouda.agents.core.Flag;

import java.util.Map;

public interface CommandLineAction {

    boolean applicable(Map<Flag, String> flags);

    void run(Map<Flag, String> flags);

}
