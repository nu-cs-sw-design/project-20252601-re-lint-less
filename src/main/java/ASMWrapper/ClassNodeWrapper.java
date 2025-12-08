package ASMWrapper;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * Wrapper for ASM's ClassNode providing a cleaner API.
 */
public class ClassNodeWrapper {

    private final ClassNode delegate;

    public ClassNodeWrapper(ClassNode delegate) {
        this.delegate = delegate;
    }

    /**
     * Get the underlying ClassNode (for direct ASM access if needed).
     */
    public ClassNode getDelegate() {
        return delegate;
    }

    /**
     * Get the internal name of the class (e.g., "com/example/MyClass").
     */
    public String getName() {
        return delegate.name;
    }

    /**
     * Get the internal name of the superclass.
     */
    public String getSuperName() {
        return delegate.superName;
    }

    /**
     * Get all implemented interfaces.
     */
    @SuppressWarnings("unchecked")
    public List<String> getInterfaces() {
        return (List<String>) delegate.interfaces;
    }

    /**
     * Get all methods in the class.
     */
    @SuppressWarnings("unchecked")
    public List<MethodNode> getMethods() {
        return (List<MethodNode>) delegate.methods;
    }

    /**
     * Get all fields in the class.
     */
    @SuppressWarnings("unchecked")
    public List<FieldNode> getFields() {
        return (List<FieldNode>) delegate.fields;
    }

    /**
     * Get the number of methods.
     */
    public int getMethodCount() {
        return delegate.methods.size();
    }

    /**
     * Get the number of fields.
     */
    public int getFieldCount() {
        return delegate.fields.size();
    }

    /**
     * Check if class has a superclass.
     */
    public boolean hasSuperClass() {
        return delegate.superName != null;
    }

    /**
     * Get access flags for the class.
     */
    public int getAccess() {
        return delegate.access;
    }
}
