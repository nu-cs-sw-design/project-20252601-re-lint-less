package Checks;

import Reporting.Reporter;
import org.objectweb.asm.tree.ClassNode;

/**
 * Detects "God Classes" that violate the Single Responsibility Principle.
 * A God Class is one that has too many methods or fields, indicating it's
 * trying to do too much and should be refactored into smaller classes.
 */
public class GodClassCheck implements Check {

    private final int maxMethods;
    private final int maxFields;

    public GodClassCheck() {
        this.maxMethods = 20; // default threshold
        this.maxFields = 10;  // default threshold
    }

    public GodClassCheck(int maxMethods, int maxFields) {
        this.maxMethods = maxMethods;
        this.maxFields = maxFields;
    }

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            int methodCount = classNode.methods.size();
            int fieldCount = classNode.fields.size();

            boolean isGodClass = false;

            if (methodCount > maxMethods) {
                reporter.report(
                        classNode.name,
                        "Class has " + methodCount + " methods (max allowed: " + maxMethods + ") - possible God Class"
                );
                isGodClass = true;
            }

            if (fieldCount > maxFields) {
                reporter.report(
                        classNode.name,
                        "Class has " + fieldCount + " fields (max allowed: " + maxFields + ") - possible God Class"
                );
                isGodClass = true;
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "GodClassCheck failed: " + e.getMessage()
            );
            return false;
        }
    }
}
