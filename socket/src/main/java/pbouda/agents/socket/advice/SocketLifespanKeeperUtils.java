package pbouda.agents.socket.advice;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.jar.asm.Opcodes;
import pbouda.agents.core.bytecode.appender.StaticFieldAssigner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SocketLifespanKeeperUtils {

    private static boolean initialized = false;

    public static final String FIELD_NAME = "lifespan";
    public static final String CLASS_NAME = "pbouda.agents.socket.advice.SocketLifespanKeeper";

    public synchronized static void initialize() {
        if (!initialized) {
            TypeDefinition generatedType = TypeDescription.Generic.Builder.parameterizedType(
                    ConcurrentHashMap.class, Integer.class, Long.class).build();

            Map<TypeDescription, byte[]> types = new ByteBuddy()
                    .subclass(Object.class)
                    .defineField(FIELD_NAME, generatedType, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
                    .initializer(new StaticFieldAssigner(CLASS_NAME, FIELD_NAME, ConcurrentHashMap.class))
                    .name(CLASS_NAME)
                    .make()
                    .getAllTypes();

            ClassInjector.UsingUnsafe.ofBootLoader()
                    .inject(types);

            initialized = true;
        }
    }

    public static void cleanup() {
        SocketLifespanKeeper.lifespan.clear();
    }
}
