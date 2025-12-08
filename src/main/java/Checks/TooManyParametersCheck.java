package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import Reporting.Reporter;

import java.util.List;

/**
 * Flags methods with too many parameters (5).
 */
public class TooManyParametersCheck implements Check {

    // Default maximum allowed parameters
    private final static int maxParams = 5;

    @Override
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            List<IMethod> methods = clazz.getMethods();

            for (IMethod method : methods) {
                if (method.getName().equals("<init>") || method.getName().equals("<clinit>")) {
                    continue;
                }

                int paramCount = method.getParameterCount();

                if (paramCount > maxParams) {
                    reporter.report(
                            clazz.getClassName(),
                            "Method '" + method.getName() + "' has " + paramCount
                                    + " parameters (max allowed: " + maxParams + ")"
                    );
                }
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    clazz.getClassName(),
                    "TooManyParametersCheck failed: " + e.getMessage()
            );
            return false;
        }
    }
}
