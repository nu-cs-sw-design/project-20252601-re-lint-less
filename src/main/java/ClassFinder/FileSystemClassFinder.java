package ClassFinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileSystemClassFinder implements ClassFinder {

    private final Path classesRoot;

    public FileSystemClassFinder(Path classesRoot) {
        this.classesRoot = classesRoot.toAbsolutePath();
    }

    @Override
    public List<String> discoverClasses(String[] inputs) throws IOException {
        List<String> classNames = new ArrayList<>();

        for (String arg : inputs) {
            File file = new File(arg);
            if (file.isDirectory()) {
                classNames.addAll(findClassFilesInDirectory(Paths.get(arg)));
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
     */
    private List<String> findClassFilesInDirectory(Path dir) throws IOException {
        Path absoluteDir = dir.toAbsolutePath();

        if (!Files.exists(absoluteDir) || !Files.isDirectory(absoluteDir)) {
            return List.of();
        }

        // Ensure the directory is actually under the classes root
        if (!absoluteDir.startsWith(classesRoot)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.walk(absoluteDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.toString().contains("$")) // Skip inner classes
                    .map(this::pathToClassName)
                    .filter(name -> name != null && !name.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Find class files matching a wildcard pattern.
     * Pattern format: "com.example.*" matches all classes in com.example package.
     * For now, keep behavior similar to your original implementation.
     */
    private List<String> findClassFilesByPattern(String pattern) {
        // For now, just return the pattern as-is, without "*".
        return List.of(pattern.replace("*", ""));
    }

    /**
     * Convert a file path to a fully qualified class name.
     */
    private String pathToClassName(Path classPath) {
        try {
            Path relativePath = classesRoot.relativize(classPath);
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
}
