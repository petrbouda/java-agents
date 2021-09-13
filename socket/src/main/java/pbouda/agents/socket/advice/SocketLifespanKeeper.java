package pbouda.agents.socket.advice;

import java.util.concurrent.ConcurrentHashMap;

public abstract class SocketLifespanKeeper {

    public static ConcurrentHashMap<Integer, Long> lifespan;

}
