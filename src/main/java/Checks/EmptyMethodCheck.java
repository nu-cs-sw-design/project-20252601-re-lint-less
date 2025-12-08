package Checks;

import Reporting.Reporter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * Flags methods that have empty bodies (no instructions).
 * This is a code smell that suggests the method either needs implementation
 * or should be removed.
 */
public class EmptyMethodCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            for (MethodNode method : classNode.methods) {
                // Skip abstract methods (they're supposed to be empty)
                if ((method.access & 0x0400) != 0) { // ACC_ABSTRACT = 0x0400
                    continue;
                }

                // Skip native methods (they have no bytecode)
                if ((method.access & 0x0100) != 0) { // ACC_NATIVE = 0x0100
                    continue;
                }

                InsnList instructions = method.instructions;

                // Check if method has no instructions or only a return instruction
                if (instructions == null || instructions.size() == 0) {
                    reporter.report(
                            classNode.name,
                            "Method '" + method.name + "' has an empty body"
                    );
                } else if (isOnlyReturn(instructions)) {
                    reporter.report(
                            classNode.name,
                            "Method '" + method.name + "' only contains a return statement"
                    );
                }
            }
            return true;

        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "EmptyMethodCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Check if the instruction list contains only a return statement.
     * Valid patterns: just a RETURN/IRETURN/etc., possibly with a label before it.
     */
    private boolean isOnlyReturn(InsnList instructions) {
        if (instructions.size() > 2) {
            return false;
        }

        int returnCount = 0;
        for (int i = 0; i < instructions.size(); i++) {
            int opcode = instructions.get(i).getOpcode();

            // Count return instructions
            if (opcode >= 172 && opcode <= 177) { // IRETURN(172) to RETURN(177)
                returnCount++;
            }
            // Ignore labels and line numbers (opcode == -1)
            else if (opcode != -1) {
                // Non-return, non-label instruction found
                return false;
            }
        }

        return returnCount == 1;
    }
}