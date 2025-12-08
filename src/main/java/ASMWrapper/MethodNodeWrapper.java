package ASMWrapper;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * Wrapper for ASM's MethodNode providing a cleaner API.
 */
public class MethodNodeWrapper {

    private final MethodNode delegate;

    public MethodNodeWrapper(MethodNode delegate) {
        this.delegate = delegate;
    }

    /**
     * Get the underlying MethodNode (for direct ASM access if needed).
     */
    public MethodNode getDelegate() {
        return delegate;
    }

    /**
     * Get the method name.
     */
    public String getName() {
        return delegate.name;
    }

    /**
     * Get the method descriptor (signature).
     */
    public String getDescriptor() {
        return delegate.desc;
    }

    /**
     * Get the instruction list.
     */
    public InsnList getInstructions() {
        return delegate.instructions;
    }

    /**
     * Get local variables.
     */
    @SuppressWarnings("unchecked")
    public List<LocalVariableNode> getLocalVariables() {
        return (List<LocalVariableNode>) delegate.localVariables;
    }

    /**
     * Check if method has any instructions.
     */
    public boolean hasInstructions() {
        return delegate.instructions != null && delegate.instructions.size() > 0;
    }

    /**
     * Get the number of instructions.
     */
    public int getInstructionCount() {
        return delegate.instructions != null ? delegate.instructions.size() : 0;
    }

    /**
     * Get access flags for the method.
     */
    public int getAccess() {
        return delegate.access;
    }

    /**
     * Check if method is static.
     */
    public boolean isStatic() {
        return AccessFlagHelper.isStatic(delegate.access);
    }

    /**
     * Check if method is abstract.
     */
    public boolean isAbstract() {
        return AccessFlagHelper.isAbstract(delegate.access);
    }

    /**
     * Check if method is native.
     */
    public boolean isNative() {
        return AccessFlagHelper.isNative(delegate.access);
    }

    /**
     * Check if method is public.
     */
    public boolean isPublic() {
        return AccessFlagHelper.isPublic(delegate.access);
    }
}
