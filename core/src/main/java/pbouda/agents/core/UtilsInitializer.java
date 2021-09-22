package pbouda.agents.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool;

import java.lang.instrument.Instrumentation;
import java.util.Map;

public abstract class UtilsInitializer {

    private static boolean INITIALIZED = false;

    /*
     * Rafael Winterhalter (StackOverflow):
     *
     * Any module predeclares its packages. In order to add something to java.base,
     * you must add a class to a package that java.base already contains such as java.lang.
     *
     * This is no longer possible using Instrumentation.appendToBootSearchPath as the
     * packages of java.base are only directed to the jmod in question.
     * Instead, you can open modules, including java.base by using the new modifyModule API.
     *
     * If you really wanted to inject classes, this is still possible using Unsafe.
     * Byte Buddy offers a factory for this in correspondence with its AgentBuilder.
     * Have a look at ClassInjector.UsingUnsafe.Factory which offers to create such an
     * injector. The Instrumentation instance is required to fall back to the JDKs
     * internal Unsafe API after the semi-public one was removed.
     */
    public static synchronized void initialize() {
        if (!INITIALIZED) {
            TypePool typePool = TypePool.Default.ofSystemLoader();

            Map<TypeDescription, byte[]> types = new ByteBuddy()
                    .redefine(
                            typePool.describe("sun.nio.ch.Utils").resolve(),
                            ClassFileLocator.ForClassLoader.ofSystemLoader())
                    .make()
                    .getAllTypes();

             ClassInjector.UsingUnsafe.ofBootLoader()
                     .inject(types);

            INITIALIZED = true;
        }
    }
}
