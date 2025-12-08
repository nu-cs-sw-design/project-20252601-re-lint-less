package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import BytecodeParser.IField;
import Reporting.Reporter;

import java.util.List;

/**
 * Detects "God Classes".
 * A God Class is one that has too many methods or fields, indicating its
 * trying to do too much and should be refactored into smaller classes.
 */
public class GodClassCheck implements Check {

    private final int maxMethods = 20;
    private final int maxFields = 10;

    @Override
    public boolean apply(IClass classNode, Reporter reporter) {
        try {
            List<IMethod> methods = classNode.getMethods();
            List<IField> fields = classNode.getFields();

            int methodCount = methods.size();
            int fieldCount = fields.size();

            if (methodCount > maxMethods) {
                reporter.report(
                        classNode.getClassName(),
                        "Class has " + methodCount + " methods (max allowed: " + maxMethods + ") - possible God Class"
                );
            }

            if (fieldCount > maxFields) {
                reporter.report(
                        classNode.getClassName(),
                        "Class has " + fieldCount + " fields (max allowed: " + maxFields + ") - possible God Class"
                );
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.getClassName(),
                    "GodClassCheck failed: " + e.getMessage()
            );
            return false;
        }
    }
}
