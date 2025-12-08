package BytecodeParser.ASM;

import BytecodeParser.IInstruction;
import BytecodeParser.ILocalVariable;
import BytecodeParser.IMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ASM-backed implementation of IMethod.
 */
final class ASMMethod implements IMethod {

    private final MethodNode node;

    ASMMethod(MethodNode node) {
        this.node = node;
    }

    @Override
    public String getName() {
        return node.name;
    }

    @Override
    public int getParameterCount() {
        return Type.getArgumentTypes(node.desc).length;
    }

    @Override
    public List<IInstruction> getInstructions() {
        List<IInstruction> instructions = new ArrayList<>();
        for (AbstractInsnNode insn : node.instructions) {
            instructions.add(new ASMInstruction(insn));
        }
        return instructions;
    }

    @Override
    public List<Integer> getOpcodes() {   // if your IMethod declares this
        List<Integer> opcodes = new ArrayList<>();
        Iterator<AbstractInsnNode> it = node.instructions.iterator();
        while (it.hasNext()) {
            opcodes.add(it.next().getOpcode());
        }
        return opcodes;
    }

    @Override
    public List<String> getInstructionTypes() { // if your IMethod declares this
        List<String> types = new ArrayList<>();
        for (AbstractInsnNode insn : node.instructions.toArray()) {
            types.add(insn.getClass().getSimpleName());
        }
        return types;
    }

    @Override
    public List<ILocalVariable> getLocalVariables() {
        if (node.localVariables == null) {
            return List.of();
        }
        return node.localVariables.stream()
                .map(lv -> new ILocalVariable() {
                    @Override
                    public String getName() {
                        return lv.name;
                    }

                    @Override
                    public int getIndex() {
                        return lv.index;
                    }

                    @Override
                    public String getType() {
                        return lv.desc;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getDescriptor() {
        return node.desc;
    }

    @Override
    public boolean isStatic() {
        return (node.access & Opcodes.ACC_STATIC) != 0;
    }
}
