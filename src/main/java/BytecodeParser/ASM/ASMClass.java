package BytecodeParser.ASM;

import BytecodeParser.IClass;
import BytecodeParser.IField;
import BytecodeParser.IMethod;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ASM-backed implementation of IClass.
 */
public final class ASMClass implements IClass {

    private final ClassNode node;

    ASMClass(ClassNode node) {
        this.node = node;
    }

    @Override
    public String getClassName() {
        return node.name;
    }

    @Override
    public List<IMethod> getMethods() {
        return node.methods.stream()
                .map(m -> new ASMMethod((MethodNode) m))
                .collect(Collectors.toList());
    }

    @Override
    public List<IField> getFields() {
        return node.fields.stream()
                .map(f -> new ASMField((FieldNode) f))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDirectInterfaces() {
        return new ArrayList<>(node.interfaces);
    }

    @Override
    public String getSuperClassName() {
        return node.superName;
    }
}
