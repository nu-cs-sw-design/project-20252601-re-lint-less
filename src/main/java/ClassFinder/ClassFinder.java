package ClassFinder;

import java.io.IOException;
import java.util.List;

public interface ClassFinder {

    /**
     * Discover class names from some inputs (e.g., CLI args).
     * @param inputs array of inputs (class names, directories, patterns, etc.)
     * @return list of fully qualified class names to lint
     */
    List<String> discoverClasses(String[] inputs) throws IOException;
}
