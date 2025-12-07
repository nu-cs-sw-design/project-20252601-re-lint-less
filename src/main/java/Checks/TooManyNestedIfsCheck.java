package Checks;

import Reporting.Reporter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;

/**
 * Flags methods whose conditional branching (if-statements) is nested
 * deeper than a configured threshold.
 */
public class TooManyNestedIfsCheck implements Check {

    private static final int MAX_NESTING = 3;

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            List<MethodNode> methods = (List<MethodNode>) classNode.methods;
            for (MethodNode method : methods) {

                if (method.name.equals("<init>") || method.name.equals("<clinit>")) {
                    continue;
                }

                int maxDepth = computeMaxIfNesting(method);
                if (maxDepth > MAX_NESTING) {
                    reporter.report(classNode.name,
                            "Method '" + method.name + "' has nested conditionals of depth "
                                    + maxDepth + " (max allowed is " + MAX_NESTING + ")."
                    );
                }
            }
            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "TooManyNestedIfsCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Estimate the maximum nesting of if-statements in a method.
     */
    private int computeMaxIfNesting(MethodNode method) {
        InsnList instruction = method.instructions;
        if (instruction == null || instruction.size() == 0) {
            return 0;
        }

        // Map each LabelNode to its instruction index
        Map<LabelNode, Integer> labelIndex = new IdentityHashMap<>();
        for (int i = 0; i < instruction.size(); i++) {
            AbstractInsnNode absInstruction = instruction.get(i);

            if (absInstruction instanceof LabelNode) {
                labelIndex.put((LabelNode) absInstruction, i);
            }
        }

        Deque<Integer> regionEndStack = new ArrayDeque<>();
        int maxDepth = 0;

        for (int i = 0; i < instruction.size(); i++) {

            // Pop any regions that have ended
            while (!regionEndStack.isEmpty() && regionEndStack.peek() <= i) {
                regionEndStack.pop();
            }

            AbstractInsnNode absInstruction = instruction.get(i);

            if (absInstruction instanceof JumpInsnNode) {
                int opcode = absInstruction.getOpcode();

                if ((opcode >= Opcodes.IFEQ && opcode <= Opcodes.IF_ACMPNE)
                        || opcode == Opcodes.IFNULL
                        || opcode == Opcodes.IFNONNULL) {
                    JumpInsnNode jump = (JumpInsnNode) absInstruction;
                    Integer targetIdx = labelIndex.get(jump.label);

                    // Only consider forward jumps
                    if (targetIdx != null && targetIdx > i) {
                        regionEndStack.push(targetIdx);
                        maxDepth = Math.max(maxDepth, regionEndStack.size());
                    }
                }
            }
        }

        return maxDepth;
    }}
