import ASMParser.Parser;
import Checks.Check;
import org.objectweb.asm.tree.ClassNode;
import Reporting.Reporter;
import Reporting.ConsoleReporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Linter {

    private final Parser parser = new Parser();
    private final List<Check> checks = new ArrayList<>();
    private static final Path CLASSES_ROOT = Paths.get("build", "classes", "java", "main");

    public Linter() {
        // default constructor, no args
    }

    public void addCheck(Check check) {
        checks.add(check);
    }

    public void addChecks(List<Check> checks) {
        this.checks.addAll(checks);
    }

    public void run(String className, Reporter reporter) {
        ClassNode node;

        try {
            node = parser.parse(className);
        } catch (IOException | RuntimeException e) {
            reporter.report(className, "Failed to parse class: " + e.getMessage());
            return;
        }

        for (Check check : checks) {
            try {
                boolean ok = check.apply(node, reporter);
                if (!ok) {
                    reporter.report(className,
                            "Check failed: " + check.getClass().getSimpleName());
                }
            } catch (RuntimeException e) {
                reporter.report(className,
                        "Check crashed: " + check.getClass().getSimpleName() +
                                " — " + e.getMessage());
            }
        }
    }

    /**
     * Run linter on multiple class names.
     * @param classNames list of fully qualified class names
     * @param reporter the reporter to use for output
     */
    public void runMultiple(List<String> classNames, Reporter reporter) {
        for (String className : classNames) {
            run(className, reporter);
        }
    }

    /**
     * Discover class names from command line arguments.
     * Supports:
     * - Fully qualified class names (e.g., "com.example.MyClass")
     * - Directories (e.g., "build/classes/java/main")
     * - Wildcard patterns (e.g., "com.example.*")
     *
     * @param args command line arguments
     * @return list of fully qualified class names to lint
     */
    public static List<String> discoverClasses(String[] args) throws IOException {
        List<String> classNames = new ArrayList<>();

        for (String arg : args) {
            File file = new File(arg);
            if (file.isDirectory()) {
                classNames.addAll(findClassFilesInDirectory(arg));
            } else if (arg.contains("*")) {
                classNames.addAll(findClassFilesByPattern(arg));
            } else {
                // Assume it's a fully qualified class name
                classNames.add(arg);
            }
        }

        return classNames;
    }

    /**
     * Find all .class files in a directory recursively and convert to FQN.
     * @param dirPath directory path to search
     * @return list of fully qualified class names
     */
    private static List<String> findClassFilesInDirectory(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath).toAbsolutePath();
        Path rootPath = CLASSES_ROOT.toAbsolutePath();

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return List.of();
        }

        // Ensure the directory is actually under the classes root
        if (!dir.startsWith(rootPath)) {
            // You can choose to return empty or throw an error. For now, just return empty.
            return List.of();
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.toString().contains("$")) // Skip inner classes
                    .map(p -> pathToClassName(rootPath, p))
                    .filter(name -> name != null && !name.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Find class files matching a wildcard pattern.
     * Pattern format: "com.example.*" matches all classes in com.example package
     * @param pattern wildcard pattern
     * @return list of fully qualified class names
     */
    private static List<String> findClassFilesByPattern(String pattern) {
        // For now, just return the pattern as-is
        // Full implementation would require classpath scanning
        return List.of(pattern.replace("*", ""));
    }

    /**
     * Convert a file path to a fully qualified class name.
     * @param rootPath the root directory (e.g., build/classes/java/main)
     * @param classPath the path to the .class file
     * @return fully qualified class name or null if conversion fails
     */
    private static String pathToClassName(Path rootPath, Path classPath) {
        try {
            Path relativePath = rootPath.relativize(classPath);
            String pathStr = relativePath.toString();

            // Remove .class extension
            if (pathStr.endsWith(".class")) {
                pathStr = pathStr.substring(0, pathStr.length() - 6);
            }

            // Convert file separators to dots
            return pathStr.replace(File.separator, ".");
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Linter <class-name|directory> [<class-name|directory> ...]");
            System.err.println("  class-name: Fully qualified class name (e.g., com.example.MyClass)");
            System.err.println("  directory:  Path to directory containing .class files");
            System.exit(1);
        }

        List<String> classNames;
        try {
            classNames = discoverClasses(args);
        } catch (IOException e) {
            System.err.println("Error discovering classes: " + e.getMessage());
            System.exit(1);
            return;
        }

        if (classNames.isEmpty()) {
            System.err.println("No classes found to lint");
            System.exit(1);
        }

        Reporter reporter = new ConsoleReporter();
        Linter linter = new Linter();
        linter.addChecks(CheckFactory.createChecks());

        linter.runMultiple(classNames, reporter);
    }
}
