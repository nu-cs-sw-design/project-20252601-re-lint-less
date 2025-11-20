package Checks;

import Reporting.Reporter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Flags interfaces that a class declares explicitly even though they are
 * already inherited from its superclass or from another implemented interface.
 */
public class RedundantInterfacesCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            List<String> directInterfaces = (List<String>) classNode.interfaces;

            if (directInterfaces == null || directInterfaces.isEmpty()) {
                return true;
            }

            Set<String> impliedInterfaces = new HashSet<>();

            // Collect interfaces from the superclass chain
            if (classNode.superName != null) {
                collectAllInterfacesFromClass(classNode.superName, impliedInterfaces, new HashSet<>());
            }

            // For each directly implemented interface, collect its superinterfaces
            for (String iface : directInterfaces) {
                collectSuperInterfaces(iface, impliedInterfaces, new HashSet<>());
            }

            for (String iface : directInterfaces) {
                if (impliedInterfaces.contains(iface)) {
                    reporter.report(
                            classNode.name,
                            "Interface '" + iface
                                    + "' is redundantly declared; it is already inherited " +
                                    "from the superclass or another implemented interface."
                    );
                }
            }

            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "RedundantInterfacesCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Recursively collect all interfaces implemented by the given class
     */
    private void collectAllInterfacesFromClass(String internalClassName,
                                               Set<String> out,
                                               Set<String> visited) throws Exception {
        if (internalClassName == null || !visited.add(internalClassName)) {
            return;
        }

        ClassNode node = readClass(internalClassName);

        List<String> interfaces = (List<String>) node.interfaces;
        if (interfaces != null) {
            for (String iface : interfaces) {
                if (out.add(iface)) {
                    // Collect that interface's superinterfaces
                    collectSuperInterfaces(iface, out, visited);
                }
            }
        }

        // Recurse up the superclass chain
        if (node.superName != null) {
            collectAllInterfacesFromClass(node.superName, out, visited);
        }
    }

    /**
     * Recursively collect all superinterfaces of the given interface.
     */
    private void collectSuperInterfaces(String internalInterfaceName,
                                        Set<String> out,
                                        Set<String> visited) throws Exception {
        if (internalInterfaceName == null || !visited.add(internalInterfaceName)) {
            return;
        }

        ClassNode ifaceNode = readClass(internalInterfaceName);

        List<String> superIfaces = (List<String>) ifaceNode.interfaces;
        if (superIfaces != null) {
            for (String superIface : superIfaces) {
                if (out.add(superIface)) {
                    collectSuperInterfaces(superIface, out, visited);
                }
            }
        }
    }

    /**
     * Helper to load a class or interface into a ClassNode
     */
    private ClassNode readClass(String internalName) throws Exception {
        ClassReader reader = new ClassReader(internalName);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);
        return node;
    }
}
