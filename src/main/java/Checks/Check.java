package Checks;

import BytecodeParser.IClass;
import org.objectweb.asm.tree.ClassNode;
import Reporting.Reporter;

public interface Check {
    /**
     * @return true if the check ran successfully,
     *         false if the check had an internal error.
     */
    boolean apply(IClass classNode, Reporter reporter);
}
