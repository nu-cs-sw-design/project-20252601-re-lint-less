package ASMParser;

import java.io.IOException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class Parser {

    /**
     * Parses a class by name using the classpath
     */
    public ClassNode parse(String className) throws IOException {
        ClassReader reader = new ClassReader(className);
        ClassNode classNode = new ClassNode();

        reader.accept(classNode, ClassReader.EXPAND_FRAMES);
        return classNode;
    }
}
