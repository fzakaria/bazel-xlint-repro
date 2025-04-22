import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * By *fail* i mean this target builds correctly even though it uses a deprecated library.
 * 
 * You can see the alternative class in DeprecatedLibTest.java which fails to build
 * although it uses the same code with the sole exception of not using jupiter static assertions.
 */
public class DeprecatedLibFailTest {

    @Test
    public void testDoSomething() {
        DeprecatedLib lib = new DeprecatedLib();
        lib.oldMethod(); // This will show a warning
    }
}