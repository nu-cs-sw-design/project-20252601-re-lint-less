package Checks;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.Type;
import Reporting.Reporter;

/**
 * Checks if methods have too many parameters.
 * Methods with excessive parameters are harder to understand and maintain.
 * Threshold: Maximum 5 parameters per method.
 */
public class TooManyParametersCheck implements Check {
    private static final int MAX_PARAMETERS = 5;

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            for (MethodNode method : classNode.methods) {
                if (method.name.equals("<init>") || method.name.equals("<clinit>")) {
                    continue;
                }

                int paramCount = countParameters(method.desc);

                if (paramCount > MAX_PARAMETERS) {
                    reporter.report(
                        classNode.name,
                        "Method '" + method.name + "' has " + paramCount +
                        " parameters (max " + MAX_PARAMETERS + " allowed)"
                    );
                }
            }
            return true;
        } catch (Exception e) {
            reporter.report(classNode.name, "TooManyParametersCheck failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Counts the number of parameters in a method descriptor.
     * Method descriptors have the format: (param types)return type
     * Example: (ILjava/lang/String;)V means 2 parameters (int, String)
     */
    private int countParameters(String descriptor) {
        Type methodType = Type.getType(descriptor);
        Type[] argumentTypes = methodType.getArgumentTypes();
        return argumentTypes.length;
    }
}