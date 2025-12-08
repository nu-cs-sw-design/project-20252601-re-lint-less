package ASMWrapper;

import org.objectweb.asm.tree.FieldNode;

/**
 * Wrapper for ASM's FieldNode providing a cleaner API.
 */
public class FieldNodeWrapper {

    private final FieldNode delegate;

    public FieldNodeWrapper(FieldNode delegate) {
        this.delegate = delegate;
    }

    /**
     * Get the underlying FieldNode (for direct ASM access if needed).
     */
    public FieldNode getDelegate() {
        return delegate;
    }

    /**
     * Get the field name.
     */
    public String getName() {
        return delegate.name;
    }

    /**
     * Get the field descriptor (type signature).
     */
    public String getDescriptor() {
        return delegate.desc;
    }

    /**
     * Get access flags for the field.
     */
    public int getAccess() {
        return delegate.access;
    }

    /**
     * Check if field is public.
     */
    public boolean isPublic() {
        return AccessFlagHelper.isPublic(delegate.access);
    }

    /**
     * Check if field is static.
     */
    public boolean isStatic() {
        return AccessFlagHelper.isStatic(delegate.access);
    }

    /**
     * Check if field is final.
     */
    public boolean isFinal() {
        return AccessFlagHelper.isFinal(delegate.access);
    }

    /**
     * Check if field is a constant (static final).
     */
    public boolean isConstant() {
        return isStatic() && isFinal();
    }
}