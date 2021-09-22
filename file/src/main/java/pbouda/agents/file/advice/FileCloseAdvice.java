package pbouda.agents.file.advice;

import net.bytebuddy.asm.Advice;
import sun.nio.ch.Utils;

import java.io.FileDescriptor;

public class FileCloseAdvice {

    private static final String LOG = """
            {"logger": "file-agent", "file_descriptor": %s, "path": "%s", "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodEnter
    static void onExit(
            @Advice.FieldValue("fd") FileDescriptor fd,
            @Advice.FieldValue("path") String path) {

        int fdValue = Utils.fdVal(fd);
        String message = "File #close: path=" + path + " fd=" + fdValue;
        String formatted = LOG.formatted(fdValue, path, Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
