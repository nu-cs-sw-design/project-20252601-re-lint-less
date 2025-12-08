package BytecodeParser.ASM;

import BytecodeParser.ILabel;
import org.objectweb.asm.tree.LabelNode;

/**
 * ASM-backed implementation of ILabel.
 */
final class ASMLabel implements ILabel {

    private final LabelNode node;

    ASMLabel(LabelNode node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ASMLabel)) return false;
        ASMLabel other = (ASMLabel) o;
        return this.node == other.node;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(node);
    }
}
