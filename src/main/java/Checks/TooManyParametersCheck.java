package Checks;

import Reporting.Reporter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Flags methods with too many parameters.
 */
public class TooManyParametersCheck implements Check {

    // Default maximum allowed parameters
    private final int maxParams;

    public TooManyParametersCheck() {
        this.maxParams = 5; // default threshold
    }

    public TooManyParametersCheck(int maxParams) {
        this.maxParams = maxParams;
    }

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            for (MethodNode method : classNode.methods) {
                // Skip constructors if you want (optional)
                // if ("<init>".equals(method.name)) continue;

                Type[] argumentTypes = Type.getArgumentTypes(method.desc);
                int paramCount = argumentTypes.length;

                if (paramCount > maxParams) {
                    reporter.report(
                            classNode.name,
                            "Method '" + method.name + "' has " + paramCount
                                    + " parameters (max allowed: " + maxParams + ")"
                    );
                }
            }
            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "TooManyParametersCheck failed: " + e.getMessage()
            );
            return false;
        }
    }
}