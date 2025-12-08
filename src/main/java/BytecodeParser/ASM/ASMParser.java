package BytecodeParser.ASM;

import BytecodeParser.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ASMParser implements IClassParser {

    @Override
    public IClass parse(String className) throws IOException {
        ClassReader reader = new ClassReader(className);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.EXPAND_FRAMES);

        return new ASMClass(classNode);
    }

    public static class ASMClass implements IClass {
        private final ClassNode node;

        ASMClass(ClassNode node) {
            this.node = node;
        }

        @Override
        public String getClassName() {
            return node.name;
        }

        @Override
        public List<IMethod> getMethods() {
            return node.methods.stream()
                    .map(ASMMethod::new)
                    .collect(Collectors.toList());
        }

        @Override
        public List<IField> getFields() {
            return node.fields.stream()
                    .map(ASMField::new)
                    .collect(Collectors.toList());
        }

        @Override
        public List<String> getDirectInterfaces() {
            return new ArrayList<>(node.interfaces);
        }

        @Override
        public String getSuperClassName() {
            return node.superName;
        }

        private static class ASMMethod implements IMethod {
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
            public List<Integer> getOpcodes() {
                List<Integer> opcodes = new ArrayList<>();
                Iterator<AbstractInsnNode> it = node.instructions.iterator();
                while (it.hasNext()) {
                    opcodes.add(it.next().getOpcode());
                }
                return opcodes;
            }

            @Override
            public List<String> getInstructionTypes() {
                List<String> types = new ArrayList<>();
                for (AbstractInsnNode insn : node.instructions.toArray()) {
                    types.add(insn.getClass().getSimpleName());
                }
                return types;
            }

            @Override
            public List<ILocalVariable> getLocalVariables() {
                if (node.localVariables == null) return List.of();
                return node.localVariables.stream()
                        .map(lv -> new ILocalVariable() {
                            @Override
                            public String getName() { return lv.name; }
                            @Override
                            public int getIndex() { return lv.index; }
                            @Override
                            public String getType() { return lv.desc; }
                        })
                        .collect(Collectors.toList()); // <- changed
            }

            @Override
            public String getDescriptor() {
                return node.desc;
            }

            public boolean isStatic() {
                return (node.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0;
            }

        }

        private static class ASMField implements IField {
            private final FieldNode node;

            ASMField(FieldNode node) {
                this.node = node;
            }

            @Override
            public String getName() {
                return node.name;
            }

            @Override
            public String getType() {
                return node.desc;
            }

            @Override
            public boolean isStatic() {
                return (node.access & Opcodes.ACC_STATIC) != 0;
            }

            @Override
            public boolean isFinal() {
                return (node.access & Opcodes.ACC_FINAL) != 0;
            }
        }

        public static class ASMInstruction implements IInstruction {
            private final AbstractInsnNode insn;

            ASMInstruction(AbstractInsnNode insn) {
                this.insn = insn;
            }

            @Override
            public int getOpcode() {
                return insn.getOpcode();
            }

            @Override
            public boolean isJump() {
                return insn instanceof JumpInsnNode
                        || insn instanceof TableSwitchInsnNode
                        || insn instanceof LookupSwitchInsnNode;
            }

            @Override
            public ILabel getJumpLabel() {
                if (insn instanceof JumpInsnNode) {
                    return new ASMLabel(((JumpInsnNode) insn).label);
                }
                // Could extend for TableSwitch/LookupSwitch later if needed
                return null;
            }

            @Override
            public boolean isPushConstant() {
                if (insn instanceof LdcInsnNode) return true;
                int opcode = insn.getOpcode();
                switch (opcode) {
                    case Opcodes.ICONST_M1: case Opcodes.ICONST_0: case Opcodes.ICONST_1:
                    case Opcodes.ICONST_2: case Opcodes.ICONST_3: case Opcodes.ICONST_4: case Opcodes.ICONST_5:
                    case Opcodes.LCONST_0: case Opcodes.LCONST_1:
                    case Opcodes.FCONST_0: case Opcodes.FCONST_1: case Opcodes.FCONST_2:
                    case Opcodes.DCONST_0: case Opcodes.DCONST_1:
                        return true;
                    case Opcodes.BIPUSH: case Opcodes.SIPUSH:
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

                    case Opcodes.BIPUSH: return ((IntInsnNode) insn).operand;
                    case Opcodes.SIPUSH: return ((IntInsnNode) insn).operand;

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

        private static class ASMLabel implements ILabel {
            private final LabelNode labelNode;

            ASMLabel(LabelNode labelNode) {
                this.labelNode = labelNode;
            }

            @Override
            public boolean equals(Object o) {
                return this == o; // identity-based
            }

            @Override
            public int hashCode() {
                return System.identityHashCode(this);
            }
        }
    }
}

