package pbouda.agents.file;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pbouda.agents.coretest.AssertOutput;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.instrument.Instrumentation;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

import static java.nio.file.StandardOpenOption.*;

public class FileTest {

    @BeforeEach
    public void setup() {
        Instrumentation inst = ByteBuddyAgent.install();
        FileAgent.premain(null, inst);
    }

    @AfterEach
    public void tearDown() {
        Instrumentation inst = ByteBuddyAgent.install();
        FileAgent.premain(null, inst);
    }

    @Test
    public void fileChannelDirect(@TempDir Path temp) throws Exception {
        Predicate<String> open = line -> line.contains("File #open");
        Predicate<String> close = line -> line.contains("File #close");

        try (var assertion = new AssertOutput(List.of(open, close))) {
            try (FileChannel ignored = FileChannel.open(temp.resolve("file.txt"), CREATE, WRITE)) {
            }
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void bufferedWriter(@TempDir Path temp) throws Exception {
        Predicate<String> open = line -> line.contains("File #open");
        Predicate<String> close = line -> line.contains("File #close");

        try (var assertion = new AssertOutput(List.of(open, close))) {
            try (Writer ignored = Files.newBufferedWriter(temp.resolve("file.txt"), CREATE_NEW)) {
            }
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void bufferedRead(@TempDir Path temp) throws Exception {
        Predicate<String> open = line -> line.contains("File #open");
        Predicate<String> close = line -> line.contains("File #close");

        Path tempFile = Files.createFile(temp.resolve("file.txt"));
        try (var assertion = new AssertOutput(List.of(open, close))) {
            try (Reader ignored = Files.newBufferedReader(tempFile)) {
            }
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void outputStream(@TempDir Path temp) throws Exception {
        Predicate<String> open = line -> line.contains("File #open");
        Predicate<String> close = line -> line.contains("File #close");

        try (var assertion = new AssertOutput(List.of(open, close))) {
            try (OutputStream ignored = Files.newOutputStream(temp.resolve("file.txt"), CREATE_NEW)) {
            }
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void inputStream(@TempDir Path temp) throws Exception {
        Predicate<String> open = line -> line.contains("File #open");
        Predicate<String> close = line -> line.contains("File #close");

        Path tempFile = Files.createFile(temp.resolve("file.txt"));
        try (var assertion = new AssertOutput(List.of(open, close))) {
            try (InputStream ignored = Files.newInputStream(tempFile)) {
            }
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }
}
