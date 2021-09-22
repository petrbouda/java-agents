package pbouda.agents.file.advice;

import net.bytebuddy.asm.Advice;
import sun.nio.ch.Utils;

import java.io.FileDescriptor;

public class FileOpenAdvice {

    private static final String LOG = """
            {"logger": "file-agent", "file_descriptor": %s, "path": "%s", "thread": "%s", "message": "%s"}""";

    @Advice.OnMethodExit
    static void onExit(
            @Advice.Argument(0) FileDescriptor fd,
            @Advice.Argument(1) String path) {

        int fdValue = Utils.fdVal(fd);
        String message = "File #open: path=" + path + " fd=" + fdValue;
        String formatted = LOG.formatted(fdValue, path, Thread.currentThread().getName(), message);
        System.out.println(formatted);
    }
}
