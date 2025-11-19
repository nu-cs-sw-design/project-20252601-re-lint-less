package Checks;

import org.objectweb.asm.tree.ClassNode;
import Reporting.Reporter;

public class PrintClassNameCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            reporter.report(classNode.name, "Visited class.");
            return true;
        } catch (Exception e) {
            reporter.report(classNode.name,
                    "PrintClassNameCheck failed: " + e.getMessage());
            return false;
        }
    }
}
