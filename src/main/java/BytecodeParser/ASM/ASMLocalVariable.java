package BytecodeParser.ASM;

import BytecodeParser.ILocalVariable;
import org.objectweb.asm.tree.LocalVariableNode;

/**
 * Concrete ASM-backed implementation of ILocalVariable.
 */
public class ASMLocalVariable implements ILocalVariable {

    private final LocalVariableNode node;

    public ASMLocalVariable(LocalVariableNode node) {
        this.node = node;
    }

    @Override
    public String getName() {
        return node.name;
    }

    @Override
    public int getIndex() {
        return node.index;
    }
}
