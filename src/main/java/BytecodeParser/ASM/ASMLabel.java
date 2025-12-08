package BytecodeParser.ASM;

import BytecodeParser.ILabel;
import org.objectweb.asm.tree.LabelNode;

/**
 * ASM-backed implementation of ILabel.
 */
final class ASMLabel implements ILabel {

    @Override
    public boolean equals(Object o) {
        return this == o; // identity-based
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
