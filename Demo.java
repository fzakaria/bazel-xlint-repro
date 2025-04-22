public class Demo {
    public void doSomething() {
        DeprecatedLib lib = new DeprecatedLib();
        lib.oldMethod(); // This will show a warning
    }
}