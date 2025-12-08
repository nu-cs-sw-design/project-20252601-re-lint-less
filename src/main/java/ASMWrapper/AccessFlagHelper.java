package ASMWrapper;

import org.objectweb.asm.Opcodes;

/**
 * Helper for checking access flags (public, private, static, final, etc.).
 */
public final class AccessFlagHelper {

    private AccessFlagHelper() {
        // Utility class
    }

    /**
     * Check if access flags indicate public visibility.
     */
    public static boolean isPublic(int access) {
        return (access & Opcodes.ACC_PUBLIC) != 0;
    }

    /**
     * Check if access flags indicate private visibility.
     */
    public static boolean isPrivate(int access) {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

    /**
     * Check if access flags indicate protected visibility.
     */
    public static boolean isProtected(int access) {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }

    /**
     * Check if access flags indicate static.
     */
    public static boolean isStatic(int access) {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    /**
     * Check if access flags indicate final.
     */
    public static boolean isFinal(int access) {
        return (access & Opcodes.ACC_FINAL) != 0;
    }

    /**
     * Check if access flags indicate abstract.
     */
    public static boolean isAbstract(int access) {
        return (access & Opcodes.ACC_ABSTRACT) != 0;
    }

    /**
     * Check if access flags indicate native.
     */
    public static boolean isNative(int access) {
        return (access & Opcodes.ACC_NATIVE) != 0;
    }

    /**
     * Check if access flags indicate synchronized.
     */
    public static boolean isSynchronized(int access) {
        return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
    }

    /**
     * Check if access flags indicate interface.
     */
    public static boolean isInterface(int access) {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }
}