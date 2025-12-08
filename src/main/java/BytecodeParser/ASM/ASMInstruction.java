package BytecodeParser.ASM;

import BytecodeParser.IInstruction;
import BytecodeParser.ILabel;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

final class ASMInstruction implements IInstruction {

    private final AbstractInsnNode insn;

    ASMInstruction(AbstractInsnNode insn) {
        this.insn = insn;
    }

    @Override
    public int getOpcode() {
        return insn.getOpcode();
    }

    @Override
    public ILabel getJumpLabel() {
        if (insn instanceof JumpInsnNode) {
            LabelNode target = ((JumpInsnNode) insn).label;
            return target != null ? new ASMLabel(target) : null;
        }
        if (insn instanceof LabelNode) {
            return new ASMLabel((LabelNode) insn);
        }
        return null;
    }

    @Override
    public boolean isPushConstant() {
        if (insn instanceof LdcInsnNode) return true;

        int opcode = insn.getOpcode();
        switch (opcode) {
            case Opcodes.ICONST_M1:
            case Opcodes.ICONST_0:
            case Opcodes.ICONST_1:
            case Opcodes.ICONST_2:
            case Opcodes.ICONST_3:
            case Opcodes.ICONST_4:
            case Opcodes.ICONST_5:
            case Opcodes.LCONST_0:
            case Opcodes.LCONST_1:
            case Opcodes.FCONST_0:
            case Opcodes.FCONST_1:
            case Opcodes.FCONST_2:
            case Opcodes.DCONST_0:
            case Opcodes.DCONST_1:
            case Opcodes.BIPUSH:
            case Opcodes.SIPUSH:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Number getConstantValue() {
        if (!isPushConstant()) return null;

        if (insn instanceof LdcInsnNode) {
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Number) return (Number) cst;
        }

        switch (insn.getOpcode()) {
            case Opcodes.ICONST_M1: return -1;
            case Opcodes.ICONST_0:  return 0;
            case Opcodes.ICONST_1:  return 1;
            case Opcodes.ICONST_2:  return 2;
            case Opcodes.ICONST_3:  return 3;
            case Opcodes.ICONST_4:  return 4;
            case Opcodes.ICONST_5:  return 5;

            case Opcodes.LCONST_0:  return 0L;
            case Opcodes.LCONST_1:  return 1L;

            case Opcodes.FCONST_0:  return 0f;
            case Opcodes.FCONST_1:  return 1f;
            case Opcodes.FCONST_2:  return 2f;

            case Opcodes.DCONST_0:  return 0d;
            case Opcodes.DCONST_1:  return 1d;

            case Opcodes.BIPUSH:    return ((IntInsnNode) insn).operand;
            case Opcodes.SIPUSH:    return ((IntInsnNode) insn).operand;

            default: return null;
        }
    }

    @Override
    public Integer getVarIndex() {
        if (insn instanceof VarInsnNode) {
            return ((VarInsnNode) insn).var;
        }
        return null;
    }
}
