# Deprecation Not Consistent Bug

Please see https://github.com/bazelbuild/bazel/issues/25927 for the issue this codebase is a reproduction for.

We have one target `//:DeprecatedLib` that includes a class with a `@Deprecation`.

```starlark
java_library(
    name = "DeprecatedLib",
    srcs = ["DeprecatedLib.java"],

)
```

```java
@Deprecated
public class DeprecatedLib {

    public void oldMethod() {
        System.out.println("This is an old, deprecated method.");
    }

    public static String oldUtility(String input) {
        return "Deprecated: " + input;
    }
}
```

For some reason, when I `import` a static method in a class that uses `DeprecatedLib` the `-Xlint:all` warning for the deprecation goes away.

# Happy Case

```starlark
java_test(
    name = "DeprecatedLibTest",
    srcs = ["DeprecatedLibTest.java"],
    test_class = "DeprecatedLibTest",
    deps = [
        ":DeprecatedLib",
    ],
)
```
```java
import org.junit.Test;

public class DeprecatedLibTest {

    @Test
    public void testDoSomething() {
        DeprecatedLib lib = new DeprecatedLib();
        lib.oldMethod(); // This will show a warning
    }
}
```
```console
> bazel build //:DeprecatedLibTest
INFO: Invocation ID: 52cb5107-e7da-40db-9aec-614324a886ac
INFO: Analyzed target //:DeprecatedLibTest (55 packages loaded, 1584 targets configured).
ERROR: /Users/fzakaria/code/playground/bazel/xlint-repro/BUILD.bazel:18:10: Building DeprecatedLibTest.jar (1 source file) failed: (Exit 1): java failed: error executing Javac command (from target //:DeprecatedLibTest) external/rules_java~~toolchains~remotejdk21_macos_aarch64/bin/java '--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED' '--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED' ... (remaining 19 arguments skipped)
DeprecatedLibTest.java:7: error: [deprecation] DeprecatedLib in unnamed package has been deprecated
        DeprecatedLib lib = new DeprecatedLib();
        ^
DeprecatedLibTest.java:7: error: [deprecation] DeprecatedLib in unnamed package has been deprecated
        DeprecatedLib lib = new DeprecatedLib();
                                ^
error: warnings found and -Werror specified
Target //:DeprecatedLibTest failed to build
Use --verbose_failures to see the command lines of failed build steps.
INFO: Elapsed time: 0.380s, Critical Path: 0.07s
INFO: 2 processes: 2 internal.
ERROR: Build did NOT complete successfully
```

# Failure Case
```starlark
java_test(
    name = "DeprecatedLibFailTest",
    srcs = ["DeprecatedLibFailTest.java"],
    test_class = "DeprecatedLibTest",
    deps = [
        ":DeprecatedLib",
        "@junit_jupiter//jar",
    ],
)
```
Please note this is the same file with `import static org.junit.jupiter.api.Assertions.assertEquals;` at the top.

```java
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
```
```console
> bazel build //:DeprecatedLibFailTest
INFO: Invocation ID: c4885c34-6355-423a-8880-aded4329332c
INFO: Analyzed target //:DeprecatedLibFailTest (56 packages loaded, 1586 targets configured).
INFO: Found 1 target...
Target //:DeprecatedLibFailTest up-to-date:
  bazel-bin/DeprecatedLibFailTest
  bazel-bin/DeprecatedLibFailTest.jar
INFO: Elapsed time: 0.305s, Critical Path: 0.02s
INFO: 4 processes: 2 disk cache hit, 2 internal.
INFO: Build completed successfully, 4 total actions
```
