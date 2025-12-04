package Checks;

import Reporting.Reporter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Flags magic numbers inside methods.
 */
public class MagicNumberCheck implements Check {

    // Allowed "non-magic" numbers
    private static final Set<Number> ALLOWED = new HashSet<>(Arrays.asList(
        -1, 0, 1
    ));

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            // Iterate through each method
            for (MethodNode method : classNode.methods) {
                InsnList instructions = method.instructions;
                if (instructions == null) continue;

                for (AbstractInsnNode insn : instructions) {

                    // ----- Integer push instructions (IntInsnNode) -----
                    if (insn instanceof IntInsnNode) {
                        IntInsnNode iinsn = (IntInsnNode) insn;
                        if (!isAllowed(iinsn.operand)) {
                            report(reporter, classNode, method, iinsn.operand);
                        }
                    }

                    // ----- Constant load instructions (LDC) -----
                    else if (insn instanceof LdcInsnNode) {
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if (isNumeric(ldc.cst)) {
                            Number value = (Number) ldc.cst;
                            if (!isAllowed(value)) {
                                report(reporter, classNode, method, value);
                            }
                        }
                    }

                    // ----- ICONST_#, FCONST_#, DCONST_#, LCONST_# -----
                    else if (insn.getType() == AbstractInsnNode.INSN) {
                        Number pushed = decodeSimpleConstant(insn.getOpcode());
                        if (pushed != null && !isAllowed(pushed)) {
                            report(reporter, classNode, method, pushed);
                        }
                    }
                }
            }

            return true;

        } catch (Exception e) {
            reporter.report(classNode.name,
                    "MagicNumberCheck failed: " + e.getMessage());
            return false;
        }
    }

    // Check if a constant is allowed
    private boolean isAllowed(Number n) {
        return ALLOWED.contains(n);
    }

    // Detects ICONST_x, FCONST_x, etc.
    private Number decodeSimpleConstant(int opcode) {
        switch (opcode) {
            case Opcodes.ICONST_M1:
                return -1;
            case Opcodes.ICONST_0:
                return 0;
            case Opcodes.ICONST_1:
                return 1;
            case Opcodes.ICONST_2:
                return 2;
            case Opcodes.ICONST_3:
                return 3;
            case Opcodes.ICONST_4:
                return 4;
            case Opcodes.ICONST_5:
                return 5;

            case Opcodes.LCONST_0:
                return 0L;
            case Opcodes.LCONST_1:
                return 1L;

            case Opcodes.FCONST_0:
                return 0f;
            case Opcodes.FCONST_1:
                return 1f;
            case Opcodes.FCONST_2:
                return 2f;

            case Opcodes.DCONST_0:
                return 0d;
            case Opcodes.DCONST_1:
                return 1d;

            default:
                return null;
        }
    }

    // Numeric test for LDC
    private boolean isNumeric(Object obj) {
        return obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Float
                || obj instanceof Double;
    }

    private void report(Reporter reporter, ClassNode classNode,
                        MethodNode method, Number value) {

        reporter.report(
                classNode.name,
                "Magic number " + value + " found in method " + method.name
        );
    }
}
