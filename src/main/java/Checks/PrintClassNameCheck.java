package Checks;

import BytecodeParser.IClass;
import Reporting.Reporter;

public class PrintClassNameCheck implements Check {

    @Override
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            reporter.report(clazz.getClassName(), "Visited class.");
            return true;
        } catch (Exception e) {
            reporter.report(clazz.getClassName(),
                    "PrintClassNameCheck failed: " + e.getMessage());
            return false;
        }
    }
}
