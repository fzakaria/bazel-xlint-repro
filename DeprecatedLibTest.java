import org.junit.Test;

public class DeprecatedLibTest {

    @Test
    public void testDoSomething() {
        DeprecatedLib lib = new DeprecatedLib();
        lib.oldMethod(); // This will show a warning
    }
}