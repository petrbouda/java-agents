package pbouda.agents.core.messaging;

public record MessageCommand(String message, Runnable action) {
}
