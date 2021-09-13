package pbouda.agents.socket.advice;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.jar.asm.Opcodes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SocketLifespanKeeperUtils {

    private static boolean initialized = false;

    public static final String FIELD_NAME = "lifespan";
    public static final String CLASS_NAME = "pbouda.agents.socket.LifespanKeeper";

    public synchronized static void initialize() {
        if (!initialized) {
            TypeDefinition generatedType = TypeDescription.Generic.Builder.parameterizedType(
                    Map.class, Object.class, Long.class).build();

            Map<TypeDescription, byte[]> types = new ByteBuddy()
                    .subclass(Object.class)
                    .defineField(SocketLifespanKeeperUtils.FIELD_NAME, generatedType,
                            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
                    .name(SocketLifespanKeeperUtils.CLASS_NAME)
                    .make()
                    .getAllTypes();

            ClassInjector.UsingUnsafe.ofBootLoader()
                    .inject(types);

            setLifespan(new ConcurrentHashMap<>());
            initialized = true;
        }
    }

    public static void cleanup() {
        setLifespan(null);
    }

    private static void setLifespan(Map<Integer, Long> map) {
        try {
            Class.forName(CLASS_NAME)
                    .getField(FIELD_NAME)
                    .set(null, map);
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
