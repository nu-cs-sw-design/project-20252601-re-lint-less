package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import BytecodeParser.IInstruction;
import Reporting.Reporter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            List<IMethod> methods = clazz.getMethods();
            if (methods == null) return true;

            for (IMethod method : methods) {
                List<IInstruction> instructions = method.getInstructions();
                if (instructions == null) continue;

                for (IInstruction insn : instructions) {
                    Number value = decodeConstant(insn);
                    if (value != null && !isAllowed(value)) {
                        reporter.report(
                                clazz.getClassName(),
                                "Magic number " + value + " found in method " + method.getName()
                        );
                    }
                }
            }

            return true;
        } catch (Exception e) {
            reporter.report(clazz.getClassName(),
                    "MagicNumberCheck failed: " + e.getMessage());
            return false;
        }
    }

    private boolean isAllowed(Number n) {
        return ALLOWED.contains(n);
    }

    /**
     * Tries to decode the instruction to a numeric constant if applicable.
     */
    private Number decodeConstant(IInstruction insn) {
        if (insn.isPushConstant()) {
            return insn.getConstantValue();
        }
        return null;
    }
}
