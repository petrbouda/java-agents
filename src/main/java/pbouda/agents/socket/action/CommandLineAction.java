package pbouda.agents.socket.action;

import pbouda.agents.socket.Flag;

import java.util.Map;

public interface CommandLineAction {

    boolean applicable(Map<Flag, String> flags);

    void run(Map<Flag, String> flags) throws Exception;

}
