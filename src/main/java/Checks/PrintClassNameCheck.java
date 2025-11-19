package Checks;

import Checks.Check;
import org.objectweb.asm.tree.ClassNode;

public class PrintClassNameCheck implements Check {
    @Override
    public void apply(ClassNode node) {
        System.out.println("Class being analyzed: " + node.name);
    }
}
