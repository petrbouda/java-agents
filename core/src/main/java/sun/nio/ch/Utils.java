package sun.nio.ch;

import java.io.FileDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public abstract class Utils {

    public static final int ERROR_FD_VALUE = Integer.MIN_VALUE;

    private static MethodHandle FD_VAL;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> clazz = Class.forName("sun.nio.ch.IOUtil");
            MethodType methodDesc = MethodType.methodType(int.class, FileDescriptor.class);
            FD_VAL = lookup.findStatic(clazz, "fdVal", methodDesc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int fdVal(FileDescriptor fd) {
        if (FD_VAL != null) {
            try {
                return (int) FD_VAL.invoke(fd);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return ERROR_FD_VALUE;
    }
}
