package pbouda.empty.agent;

import com.sun.tools.attach.VirtualMachine;

import java.net.URISyntaxException;

public class Start {

    public static void main(String[] args) throws Exception {
        String pid = String.valueOf(args[0]);
        VirtualMachine vm = VirtualMachine.attach(pid);
        try {
            vm.loadAgent(agentPath());
        } finally {
            vm.detach();
        }
    }

    private static String agentPath() throws URISyntaxException {
        return Start.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();
    }
}
