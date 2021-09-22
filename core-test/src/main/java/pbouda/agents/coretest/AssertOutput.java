package pbouda.agents.coretest;

import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

public class AssertOutput implements AutoCloseable {

    private final PrintStream old;
    private final List<Predicate<String>> predicates;
    private final ByteArrayOutputStream content;

    public AssertOutput(Predicate<String> predicate) {
        this(List.of(predicate));
    }

    public AssertOutput(List<Predicate<String>> predicates) {
        this.predicates = predicates;
        this.content = new ByteArrayOutputStream();
        this.old = System.out;
        System.setOut(new PrintStream(content));
    }

    public void waitForAssertion(Duration duration) {
        try {
            long start = System.nanoTime();
            BufferedReader reader = new BufferedReader(new StringReader(content.toString()));

            int cursor = 0;
            for (String line = reader.readLine(); (System.nanoTime() - start) < duration.toNanos(); line = reader.readLine()) {
                if (line != null && predicates.get(cursor).test(line)) {
                    if (++cursor == predicates.size()) {
                        return;
                    }
                }
            }
            Assertions.fail();
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    @Override
    public void close() {
        System.setOut(old);
    }
}