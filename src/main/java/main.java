import BytecodeParser.ASM.ASMParser;
import BytecodeParser.IClassParser;
import BytecodeParser.Parser;
import Checks.Check;
import Checks.CheckFactory;
import ClassFinder.ClassFinder;
import ClassFinder.FileSystemClassFinder;
import Reporting.ConsoleReporter;
import Reporting.Reporter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class main {

    private static final Path CLASSES_ROOT =
            Paths.get("build", "classes", "java", "main");

    public static void main(String[] args) {
        // 1. Validate CLI args
        if (args.length < 1) {
            System.err.println("Usage: java Linter <class-name|directory> [<class-name|directory> ...]");
            System.err.println("  class-name: Fully qualified class name (e.g., com.example.MyClass)");
            System.err.println("  directory:  Path to directory containing .class files");
            System.exit(1);
        }

        // 2. Wire up dependencies
        IClassParser asmParser = new ASMParser();
        Parser parser = new Parser(asmParser);
        List<Check> checks = CheckFactory.createChecks();
        Linter linter = new Linter(parser, checks);
        ClassFinder classFinder = new FileSystemClassFinder(CLASSES_ROOT);
        Reporter reporter = new ConsoleReporter();

        // 3. Discover classes from input
        List<String> classNames;
        try {
            classNames = classFinder.discoverClasses(args);
        } catch (IOException e) {
            System.err.println("Error discovering classes: " + e.getMessage());
            System.exit(1);
            return;
        }

        if (classNames.isEmpty()) {
            System.err.println("No classes found to lint");
            System.exit(1);
        }

        // 4. Run the linter
        linter.runMultiple(classNames, reporter);
    }
}
