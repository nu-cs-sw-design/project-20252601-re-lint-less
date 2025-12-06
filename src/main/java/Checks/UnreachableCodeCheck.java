package Checks;

import Reporting.Reporter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Flags unreachable code blocks (instructions after RETURN or other terminal instructions).
 */
public class UnreachableCodeCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            for (MethodNode method : classNode.methods) {
                InsnList instructions = method.instructions;
                if (instructions == null || instructions.size() == 0) continue;

                boolean unreachable = false;
                Set<LabelNode> jumpTargets = new HashSet<>();

                // Collect all jump targets (labels)
                for (AbstractInsnNode insn : instructions) {
                    if (insn instanceof JumpInsnNode) {
                        JumpInsnNode jumpInsn = (JumpInsnNode) insn;
                        jumpTargets.add(jumpInsn.label);
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

                for (AbstractInsnNode insn : instructions) {

                    if (insn instanceof LabelNode) {
                        LabelNode label = (LabelNode) insn;
                        // If label is a jump target, code here is reachable
                        if (jumpTargets.contains(label)) {
                            unreachable = false;
                        }
                    }

                    if (unreachable) {
                        reporter.report(
                                classNode.name,
                                "Unreachable code detected in method '" + method.name + "'"
                        );
                        // Only report once per unreachable block
                        unreachable = false;
                    }

                    // Check for terminal instructions
                    if (isTerminal(insn)) {
                        unreachable = true;
                    }
                }
            }
            return true;

        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "UnreachableCodeCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    // Returns true if the instruction ends normal control flow
    private boolean isTerminal(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        if (opcode == -1) return false; // not a real instruction
        switch (opcode) {
            case Opcodes.RETURN:
            case Opcodes.IRETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.ARETURN:
            case Opcodes.ATHROW:
                return true;
            default:
                return false;
        }
    }
}
