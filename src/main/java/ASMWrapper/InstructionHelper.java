package ASMWrapper;

import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper for analyzing bytecode instructions.
 */
public final class InstructionHelper {

    private InstructionHelper() {
        // Utility class
    }

    /**
     * Check if an instruction is a numeric constant load.
     */
    public static boolean isNumericConstant(AbstractInsnNode insn) {
        if (insn instanceof IntInsnNode || insn instanceof LdcInsnNode) {
            return true;
        }

        int opcode = insn.getOpcode();
        return OpcodeHelper.isIntConstant(opcode)
                || OpcodeHelper.isLongConstant(opcode)
                || OpcodeHelper.isFloatConstant(opcode)
                || OpcodeHelper.isDoubleConstant(opcode);
    }

    /**
     * Extract numeric value from a constant instruction.
     * Returns null if not a numeric constant.
     */
    public static Number extractNumericConstant(AbstractInsnNode insn) {
        // Check IntInsnNode (BIPUSH, SIPUSH)
        if (insn instanceof IntInsnNode) {
            return ((IntInsnNode) insn).operand;
        }

        // Check LdcInsnNode (LDC instruction for larger constants)
        if (insn instanceof LdcInsnNode) {
            Object constant = ((LdcInsnNode) insn).cst;
            if (constant instanceof Number) {
                return (Number) constant;
            }
            return null;
        }

        // Check simple constant opcodes
        int opcode = insn.getOpcode();
        Integer intVal = OpcodeHelper.decodeIntConstant(opcode);
        if (intVal != null) return intVal;

        Long longVal = OpcodeHelper.decodeLongConstant(opcode);
        if (longVal != null) return longVal;

        Float floatVal = OpcodeHelper.decodeFloatConstant(opcode);
        if (floatVal != null) return floatVal;

        Double doubleVal = OpcodeHelper.decodeDoubleConstant(opcode);
        if (doubleVal != null) return doubleVal;

        return null;
    }

    /**
     * Collect all jump targets (labels) from a method's instructions.
     */
    public static Set<LabelNode> collectJumpTargets(InsnList instructions) {
        Set<LabelNode> jumpTargets = new HashSet<>();

        for (AbstractInsnNode insn : instructions) {
            if (insn instanceof JumpInsnNode) {
                jumpTargets.add(((JumpInsnNode) insn).label);
            } else if (insn instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode tableSwitch = (TableSwitchInsnNode) insn;
                jumpTargets.add(tableSwitch.dflt);
                jumpTargets.addAll(tableSwitch.labels);
            } else if (insn instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode lookupSwitch = (LookupSwitchInsnNode) insn;
                jumpTargets.add(lookupSwitch.dflt);
                jumpTargets.addAll(lookupSwitch.labels);
            }
        }

        return jumpTargets;
    }

    /**
     * Check if an instruction is a label (jump target).
     */
    public static boolean isLabel(AbstractInsnNode insn) {
        return insn instanceof LabelNode;
    }

    /**
     * Check if an instruction is a jump instruction.
     */
    public static boolean isJump(AbstractInsnNode insn) {
        return insn instanceof JumpInsnNode;
    }

    /**
     * Get the target label from a jump instruction.
     * Returns null if not a jump instruction.
     */
    public static LabelNode getJumpTarget(AbstractInsnNode insn) {
        if (insn instanceof JumpInsnNode) {
            return ((JumpInsnNode) insn).label;
        }
        return null;
    }

    /**
     * Check if instruction is a variable load/store.
     */
    public static boolean isVarInstruction(AbstractInsnNode insn) {
        return insn instanceof VarInsnNode;
    }

    /**
     * Get variable index from a variable instruction.
     * Returns -1 if not a variable instruction.
     */
    public static int getVarIndex(AbstractInsnNode insn) {
        if (insn instanceof VarInsnNode) {
            return ((VarInsnNode) insn).var;
        }
        return -1;
    }

    /**
     * Count the number of real instructions (excluding labels and line numbers).
     */
    public static int countRealInstructions(InsnList instructions) {
        int count = 0;
        for (AbstractInsnNode insn : instructions) {
            if (insn.getOpcode() != -1) { // -1 means pseudo-instruction (label, line number)
                count++;
            }
        }
        return count;
    }
}