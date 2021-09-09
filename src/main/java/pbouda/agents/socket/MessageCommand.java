package pbouda.agents.socket;

public record MessageCommand(String message, Runnable action) {
}
