package BytecodeParser.ASM;

import BytecodeParser.IField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

/**
 * ASM-backed implementation of IField.
 */
final class ASMField implements IField {

    private final FieldNode node;

    ASMField(FieldNode node) {
        this.node = node;
    }

    @Override
    public String getName() {
        return node.name;
    }

    @Override
    public String getType() {
        return node.desc;
    }

    @Override
    public boolean isStatic() {
        return (node.access & Opcodes.ACC_STATIC) != 0;
    }

    @Override
    public boolean isFinal() {
        return (node.access & Opcodes.ACC_FINAL) != 0;
    }

    @Override
    public boolean isPublic() {
        return (node.access & Opcodes.ACC_PUBLIC) != 0;
    }
}
