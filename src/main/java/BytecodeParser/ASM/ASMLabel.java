package BytecodeParser.ASM;

import BytecodeParser.ILabel;
import org.objectweb.asm.tree.LabelNode;

/**
 * ASM-backed implementation of ILabel.
 * Identity-based equality and hashCode.
 */
final class ASMLabel implements ILabel {

    private final LabelNode labelNode;

    ASMLabel(LabelNode labelNode) {
        this.labelNode = labelNode;
    }

    @Override
    public boolean equals(Object o) {
        return this == o; // identity-based
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
