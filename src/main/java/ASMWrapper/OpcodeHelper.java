package ASMWrapper;

import org.objectweb.asm.Opcodes;

/**
 * Helper for checking and categorizing bytecode opcodes.
 */
public final class OpcodeHelper {

    private OpcodeHelper() {
        // Utility class
    }

    /**
     * Check if opcode is a return instruction.
     */
    public static boolean isReturn(int opcode) {
        return opcode == Opcodes.RETURN
                || opcode == Opcodes.IRETURN
                || opcode == Opcodes.LRETURN
                || opcode == Opcodes.FRETURN
                || opcode == Opcodes.DRETURN
                || opcode == Opcodes.ARETURN;
    }

    /**
     * Check if opcode is a throw instruction.
     */
    public static boolean isThrow(int opcode) {
        return opcode == Opcodes.ATHROW;
    }

    /**
     * Check if opcode is a terminal instruction (ends control flow).
     */
    public static boolean isTerminal(int opcode) {
        return isReturn(opcode) || isThrow(opcode);
    }

    /**
     * Check if opcode is a load instruction (ILOAD, LLOAD, etc.).
     */
    public static boolean isLoad(int opcode) {
        return opcode == Opcodes.ILOAD
                || opcode == Opcodes.LLOAD
                || opcode == Opcodes.FLOAD
                || opcode == Opcodes.DLOAD
                || opcode == Opcodes.ALOAD;
    }

    /**
     * Check if opcode is a store instruction (ISTORE, LSTORE, etc.).
     */
    public static boolean isStore(int opcode) {
        return opcode == Opcodes.ISTORE
                || opcode == Opcodes.LSTORE
                || opcode == Opcodes.FSTORE
                || opcode == Opcodes.DSTORE
                || opcode == Opcodes.ASTORE;
    }

    /**
     * Check if opcode is a conditional branch (IF_EQ, IF_NE, etc.).
     */
    public static boolean isConditionalBranch(int opcode) {
        return opcode == Opcodes.IFEQ
                || opcode == Opcodes.IFNE
                || opcode == Opcodes.IFLT
                || opcode == Opcodes.IFGE
                || opcode == Opcodes.IFGT
                || opcode == Opcodes.IFLE
                || opcode == Opcodes.IF_ICMPEQ
                || opcode == Opcodes.IF_ICMPNE
                || opcode == Opcodes.IF_ICMPLT
                || opcode == Opcodes.IF_ICMPGE
                || opcode == Opcodes.IF_ICMPGT
                || opcode == Opcodes.IF_ICMPLE
                || opcode == Opcodes.IF_ACMPEQ
                || opcode == Opcodes.IF_ACMPNE
                || opcode == Opcodes.IFNULL
                || opcode == Opcodes.IFNONNULL;
    }

    /**
     * Check if opcode is an integer constant (ICONST_M1 through ICONST_5).
     */
    public static boolean isIntConstant(int opcode) {
        return opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5;
    }

    /**
     * Decode simple integer constant opcodes to their values.
     * Returns null if not a simple constant.
     */
    public static Integer decodeIntConstant(int opcode) {
        switch (opcode) {
            case Opcodes.ICONST_M1: return -1;
            case Opcodes.ICONST_0: return 0;
            case Opcodes.ICONST_1: return 1;
            case Opcodes.ICONST_2: return 2;
            case Opcodes.ICONST_3: return 3;
            case Opcodes.ICONST_4: return 4;
            case Opcodes.ICONST_5: return 5;
            default: return null;
        }
    }

    /**
     * Check if opcode is a long constant (LCONST_0 or LCONST_1).
     */
    public static boolean isLongConstant(int opcode) {
        return opcode == Opcodes.LCONST_0 || opcode == Opcodes.LCONST_1;
    }

    /**
     * Decode simple long constant opcodes to their values.
     */
    public static Long decodeLongConstant(int opcode) {
        switch (opcode) {
            case Opcodes.LCONST_0: return 0L;
            case Opcodes.LCONST_1: return 1L;
            default: return null;
        }
    }

    /**
     * Check if opcode is a float constant (FCONST_0, FCONST_1, FCONST_2).
     */
    public static boolean isFloatConstant(int opcode) {
        return opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2;
    }

    /**
     * Decode simple float constant opcodes to their values.
     */
    public static Float decodeFloatConstant(int opcode) {
        switch (opcode) {
            case Opcodes.FCONST_0: return 0.0f;
            case Opcodes.FCONST_1: return 1.0f;
            case Opcodes.FCONST_2: return 2.0f;
            default: return null;
        }
    }

    /**
     * Check if opcode is a double constant (DCONST_0 or DCONST_1).
     */
    public static boolean isDoubleConstant(int opcode) {
        return opcode == Opcodes.DCONST_0 || opcode == Opcodes.DCONST_1;
    }

    /**
     * Decode simple double constant opcodes to their values.
     */
    public static Double decodeDoubleConstant(int opcode) {
        switch (opcode) {
            case Opcodes.DCONST_0: return 0.0;
            case Opcodes.DCONST_1: return 1.0;
            default: return null;
        }
    }
}