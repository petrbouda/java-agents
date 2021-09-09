package pbouda.agents.socket;

import java.io.*;
import java.time.Duration;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.fail;

public class AssertOutput implements AutoCloseable {

    private final PrintStream old;
    private final Predicate<String> predicate;
    private final ByteArrayOutputStream content;

    public AssertOutput(Predicate<String> predicate) {
        this.predicate = predicate;
        this.content = new ByteArrayOutputStream();
        this.old = System.out;
        System.setOut(new PrintStream(content));
    }

    public void waitForAssertion(Duration duration) {
        try {
            long start = System.nanoTime();
            BufferedReader reader = new BufferedReader(new StringReader(content.toString()));

            for (String line = reader.readLine(); (System.nanoTime() - start) < duration.toNanos(); line = reader.readLine()) {
                if (line != null && predicate.test(line)) {
                    return;
                }
            }
            fail();
        } catch (IOException e) {
            fail(e);
        }
    }

    @Override
    public void close() {
        System.setOut(old);
    }
}