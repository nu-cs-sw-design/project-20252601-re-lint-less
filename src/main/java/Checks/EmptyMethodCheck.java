package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import BytecodeParser.IInstruction;
import Reporting.Reporter;

import java.util.List;

/**
 * Flags methods that have empty bodies (no instructions).
 */
public class EmptyMethodCheck implements Check {

    @Override
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            for (IMethod method : clazz.getMethods()) {

                List<IInstruction> instructions = method.getInstructions();

                if (instructions == null || instructions.isEmpty()) {
                    reporter.report(
                            clazz.getClassName(),
                            "Method '" + method.getName() + "' has an empty body"
                    );
                }
                else if (isOnlyReturn(instructions)) {
                    reporter.report(
                            clazz.getClassName(),
                            "Method '" + method.getName() + "' only contains a return statement"
                    );
                }
            }
            return true;

        } catch (Exception e) {
            reporter.report(
                    clazz.getClassName(),
                    "EmptyMethodCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Check if the instruction list contains only a return statement.
     */
    private boolean isOnlyReturn(List<IInstruction> instructions) {
        if (instructions.isEmpty()) {
            return false;
        }

        int returnCount = 0;

        for (IInstruction insn : instructions) {
            int opcode = insn.getOpcode();

            // Count return instructions: IRETURN(172) .. RETURN(177)
            if (opcode >= 172 && opcode <= 177) {
                returnCount++;
            }
            else if (opcode != -1) {
                return false;
            }
        }

        return returnCount == 1;
    }
}
