package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import BytecodeParser.IInstruction;
import BytecodeParser.ILabel;
import Reporting.Reporter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Flags methods whose conditional branching (if-statements) is nested
 * deeper than a configured threshold.
 */
public class TooManyNestedIfsCheck implements Check {

    private static final int MAX_NESTING = 3;

    @Override
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            for (IMethod method : clazz.getMethods()) {

                // Skip constructors/static initializers
                if (method.getName().equals("<init>") || method.getName().equals("<clinit>")) {
                    continue;
                }

                int maxDepth = computeMaxIfNesting(method);
                if (maxDepth > MAX_NESTING) {
                    reporter.report(
                            clazz.getClassName(),
                            "Method '" + method.getName() + "' has nested conditionals of depth "
                                    + maxDepth + " (max allowed is " + MAX_NESTING + ")."
                    );
                }
            }
            return true;
        } catch (Exception e) {
            reporter.report(
                    clazz.getClassName(),
                    "TooManyNestedIfsCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Estimate the maximum nesting of if-statements in a method.
     */
    private int computeMaxIfNesting(IMethod method) {
        List<IInstruction> instructions = method.getInstructions();
        if (instructions == null || instructions.isEmpty()) {
            return 0;
        }

        // Map jump labels to their instruction index
        // (We only handle simple forward jumps for if-statements)
        Deque<Integer> regionEndStack = new ArrayDeque<>();
        int maxDepth = 0;

        for (int i = 0; i < instructions.size(); i++) {
            IInstruction insn = instructions.get(i);

            // Pop any regions that have ended
            while (!regionEndStack.isEmpty() && regionEndStack.peek() <= i) {
                regionEndStack.pop();
            }

            if (isConditionalJump(insn)) {
                ILabel target = insn.getJumpLabel();
                if (target != null) {
                    // Find the target index
                    int targetIndex = findLabelIndex(instructions, target);
                    if (targetIndex > i) { // forward jump
                        regionEndStack.push(targetIndex);
                        maxDepth = Math.max(maxDepth, regionEndStack.size());
                    }
                }
            }
        }

        return maxDepth;
    }

    private boolean isConditionalJump(IInstruction insn) {
        int opcode = insn.getOpcode();
        // Opcodes for simple if-statements
        return (opcode >= 153 && opcode <= 166)  // IFEQ..IF_ACMPNE
                || opcode == 198 // IFNULL
                || opcode == 199; // IFNONNULL
    }

    private int findLabelIndex(List<IInstruction> instructions, ILabel targetLabel) {
        for (int i = 0; i < instructions.size(); i++) {
            IInstruction insn = instructions.get(i);
            ILabel jumpLabel = insn.getJumpLabel();
            if (jumpLabel != null && jumpLabel.equals(targetLabel)) {
                return i;
            }
        }
        return -1;
    }

}
