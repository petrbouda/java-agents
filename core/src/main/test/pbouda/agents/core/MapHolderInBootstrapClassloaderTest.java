package pbouda.agents.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class MapHolderInBootstrapClassloaderTest {

    @Test
    public void loadingIntoBootstrapClassloader() {
        MapHolderUtils.initialize("pbouda.agents.core.MapHolder", Object.class, Object.class);
        ClassLoader classLoader = MapHolder.class.getClassLoader();
        assertNull(classLoader);
    }
}