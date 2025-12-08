package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IClassParser;
import Reporting.Reporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedundantInterfacesCheck implements Check {

    private final IClassParser classParser;

    public RedundantInterfacesCheck(IClassParser classParser) {
        this.classParser = classParser;
    }

    @Override
    public boolean apply(IClass classNode, Reporter reporter) {
        try {
            List<String> directInterfaces = classNode.getDirectInterfaces();
            if (directInterfaces == null || directInterfaces.isEmpty()) return true;

            Set<String> impliedInterfaces = new HashSet<>();
            if (classNode.getSuperClassName() != null) {
                collectAllInterfacesFromClass(classNode.getSuperClassName(), impliedInterfaces, new HashSet<>());
            }

            for (String iface : directInterfaces) {
                collectSuperInterfaces(iface, impliedInterfaces, new HashSet<>());
            }

            for (String iface : directInterfaces) {
                if (impliedInterfaces.contains(iface)) {
                    reporter.report(
                            classNode.getClassName(),
                            "Interface '" + iface
                                    + "' is redundantly declared; it is already inherited " +
                                    "from the superclass or another implemented interface."
                    );
                }
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.getClassName(),
                    "RedundantInterfacesCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    private void collectAllInterfacesFromClass(String className,
                                               Set<String> out,
                                               Set<String> visited) throws Exception {
        if (className == null || !visited.add(className)) return;

        IClass cls = classParser.parse(className);   // use the interface
        List<String> interfaces = cls.getDirectInterfaces();
        if (interfaces != null) {
            for (String iface : interfaces) {
                if (out.add(iface)) {
                    collectSuperInterfaces(iface, out, visited);
                }
            }
        }

        if (cls.getSuperClassName() != null) {
            collectAllInterfacesFromClass(cls.getSuperClassName(), out, visited);
        }
    }

    private void collectSuperInterfaces(String ifaceName,
                                        Set<String> out,
                                        Set<String> visited) throws Exception {
        if (ifaceName == null || !visited.add(ifaceName)) return;

        IClass iface = classParser.parse(ifaceName);   // use the interface
        List<String> superIfaces = iface.getDirectInterfaces();
        if (superIfaces != null) {
            for (String superIface : superIfaces) {
                if (out.add(superIface)) {
                    collectSuperInterfaces(superIface, out, visited);
                }
            }
        }
    }
}