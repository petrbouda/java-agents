package pbouda.agents.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.jar.asm.Opcodes;
import pbouda.agents.core.bytecode.appender.StaticFieldAssigner;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MapHolderUtils {

    private static final Set<String> INITIALIZED = new HashSet<>();

    public static final String DEFAULT_FIELD_NAME = "data";

    public synchronized static void initialize(String className, Class<?> keyClazz, Class<?> valueClazz) {
        if (!INITIALIZED.contains(className)) {
            TypeDefinition generatedType = TypeDescription.Generic.Builder.parameterizedType(
                    ConcurrentHashMap.class, keyClazz, valueClazz).build();

            Map<TypeDescription, byte[]> types = new ByteBuddy()
                    .subclass(Object.class)
                    .defineField(DEFAULT_FIELD_NAME, generatedType, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
                    .initializer(new StaticFieldAssigner(className, DEFAULT_FIELD_NAME, ConcurrentHashMap.class))
                    .name(className)
                    .make()
                    .getAllTypes();

            ClassInjector.UsingUnsafe.ofBootLoader()
                    .inject(types);

            INITIALIZED.add(className);
        }
    }

    public synchronized static void reset(String className) {
        if (INITIALIZED.contains(className)) {
            try {
                Class<?> clazz = Class.forName(className);
                Field field = clazz.getField(DEFAULT_FIELD_NAME);
                ConcurrentHashMap<?, ?> map = (ConcurrentHashMap<?, ?>) field.get(null);
                map.clear();
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
