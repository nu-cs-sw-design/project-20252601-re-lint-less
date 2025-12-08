import ClassFinder.ClassFinder;
import ClassFinder.FileSystemClassFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassFinderTest {

    private Path classesRoot;
    private ClassFinder classFinder;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        classesRoot = tempDir;
        classFinder = new FileSystemClassFinder(classesRoot);
    }

    @Test
    void testDiscoverClasses_withSingleClassName() throws IOException {
        String[] args = {"examples.PublicFieldExample"};

        List<String> discovered = classFinder.discoverClasses(args);

        assertEquals(1, discovered.size());
        assertEquals("examples.PublicFieldExample", discovered.get(0));
    }

    @Test
    void testDiscoverClasses_withMultipleClassNames() throws IOException {
        String[] args = {
                "examples.PublicFieldExample",
                "examples.MagicNumbersExample"
        };

        List<String> discovered = classFinder.discoverClasses(args);

        assertEquals(2, discovered.size());
        assertTrue(discovered.contains("examples.PublicFieldExample"));
        assertTrue(discovered.contains("examples.MagicNumbersExample"));
    }

    @Test
    void testDiscoverClasses_withDirectory(@TempDir Path tempDir) throws IOException {
        ClassFinder finder = new FileSystemClassFinder(tempDir);

        Path packageDir = tempDir.resolve("com").resolve("example");
        Files.createDirectories(packageDir);

        Path classFile1 = packageDir.resolve("TestClass1.class");
        Path classFile2 = packageDir.resolve("TestClass2.class");
        Files.createFile(classFile1);
        Files.createFile(classFile2);

        String[] args = {tempDir.toString()};

        List<String> discovered = finder.discoverClasses(args);

        assertEquals(2, discovered.size());
        assertTrue(discovered.contains("com.example.TestClass1"));
        assertTrue(discovered.contains("com.example.TestClass2"));
    }

    @Test
    void testDiscoverClasses_withDirectory_skipsInnerClasses(@TempDir Path tempDir) throws IOException {
        ClassFinder finder = new FileSystemClassFinder(tempDir);

        Path packageDir = tempDir.resolve("com").resolve("example");
        Files.createDirectories(packageDir);

        Path regularClass = packageDir.resolve("TestClass.class");
        Path innerClass = packageDir.resolve("TestClass$Inner.class");
        Path anonymousClass = packageDir.resolve("TestClass$1.class");
        Files.createFile(regularClass);
        Files.createFile(innerClass);
        Files.createFile(anonymousClass);

        String[] args = {tempDir.toString()};

        List<String> discovered = finder.discoverClasses(args);

        // Should only find the regular class, not inner or anonymous classes
        assertEquals(1, discovered.size());
        assertEquals("com.example.TestClass", discovered.get(0));
        assertFalse(discovered.stream().anyMatch(name -> name.contains("$")));
    }

    @Test
    void testDiscoverClasses_withNonExistentDirectory() throws IOException {
        String[] args = {"/non/existent/directory"};

        List<String> discovered = classFinder.discoverClasses(args);

        // Non-existent paths that aren't directories are treated as class names
        assertEquals(1, discovered.size());
        assertEquals("/non/existent/directory", discovered.get(0));
    }

    @Test
    void testDiscoverClasses_mixedClassNamesAndDirectory(@TempDir Path tempDir) throws IOException {
        ClassFinder finder = new FileSystemClassFinder(tempDir);

        Path packageDir = tempDir.resolve("com").resolve("test");
        Files.createDirectories(packageDir);
        Files.createFile(packageDir.resolve("DirClass.class"));

        String[] args = {
                "examples.PublicFieldExample",
                tempDir.toString()
        };

        List<String> discovered = finder.discoverClasses(args);

        assertEquals(2, discovered.size());
        assertTrue(discovered.contains("examples.PublicFieldExample"));
        assertTrue(discovered.contains("com.test.DirClass"));
    }

    @Test
    void testDiscoverClasses_emptyDirectory(@TempDir Path tempDir) throws IOException {
        ClassFinder finder = new FileSystemClassFinder(tempDir);
        String[] args = {tempDir.toString()};

        List<String> discovered = finder.discoverClasses(args);

        assertEquals(0, discovered.size());
    }

    @Test
    void testDiscoverClasses_directoryWithNonClassFiles(@TempDir Path tempDir) throws IOException {
        ClassFinder finder = new FileSystemClassFinder(tempDir);

        // Create various non-class files
        Path packageDir = tempDir.resolve("com").resolve("example");
        Files.createDirectories(packageDir);

        Files.createFile(packageDir.resolve("README.md"));
        Files.createFile(packageDir.resolve("test.java"));
        Files.createFile(packageDir.resolve("config.xml"));
        Files.createFile(packageDir.resolve("ValidClass.class"));

        String[] args = {tempDir.toString()};

        List<String> discovered = finder.discoverClasses(args);

        // Should only find the .class file
        assertEquals(1, discovered.size());
        assertEquals("com.example.ValidClass", discovered.get(0));
    }

    @Test
    void testDiscoverClasses_nestedDirectories(@TempDir Path tempDir) throws IOException {
        ClassFinder finder = new FileSystemClassFinder(tempDir);

        Path deepPackage = tempDir.resolve("org").resolve("example").resolve("util").resolve("internal");
        Files.createDirectories(deepPackage);

        Files.createFile(deepPackage.resolve("Helper.class"));

        String[] args = {tempDir.toString()};

        List<String> discovered = finder.discoverClasses(args);

        assertEquals(1, discovered.size());
        assertEquals("org.example.util.internal.Helper", discovered.get(0));
    }

    @Test
    void testDiscoverClasses_emptyArgs() throws IOException {
        String[] args = {};

        List<String> discovered = classFinder.discoverClasses(args);

        assertEquals(0, discovered.size());
    }

    @Test
    void testDiscoverClasses_directoryOutsideClassesRoot(@TempDir Path otherDir) throws IOException {
        Path outsidePackage = otherDir.resolve("outside");
        Files.createDirectories(outsidePackage);
        Files.createFile(outsidePackage.resolve("External.class"));

        String[] args = {otherDir.toString()};

        List<String> discovered = classFinder.discoverClasses(args);

        assertEquals(0, discovered.size());
    }
}
