package BytecodeParser.ASM;

import BytecodeParser.IClass;
import BytecodeParser.IClassParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

/**
 * Adapter between ASM's ClassReader and the IClassParser abstraction.
 */
public final class ASMParser implements IClassParser {

    @Override
    public IClass parse(String className) throws IOException {
        ClassReader reader = new ClassReader(className);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);
        return new ASMClass(classNode);
    }
}
