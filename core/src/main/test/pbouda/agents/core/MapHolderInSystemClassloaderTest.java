package pbouda.agents.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class MapHolderInSystemClassloaderTest {

    @Test
    public void loadingIntoSystemClassloader() {
        ClassLoader classLoader = MapHolder.class.getClassLoader();
        assertSame(ClassLoader.getSystemClassLoader(), classLoader);
    }
}