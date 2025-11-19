package Checks;

import org.objectweb.asm.tree.ClassNode;

/**
 * A single lint check that inspects an ASM ClassNode.
 * Concrete implementations live elsewhere.
 */
public interface Check {

    /**
     * Apply this check to the given class.
     * Implementations are free to report issues however they want
     * @param classNode ASM tree representation of the class
     */
    void apply(ClassNode classNode);
}
