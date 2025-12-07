package Checks;

import Reporting.Reporter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Checks that Java naming conventions are followed:
 * - Classes should use PascalCase (UpperCamelCase)
 * - Methods should use camelCase (lowerCamelCase)
 * - Fields should use camelCase, except constants which should be UPPER_SNAKE_CASE
 * - Constants are defined as static final fields
 */
public class NamingConventionCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            checkClassName(classNode, reporter);
            checkMethodNames(classNode, reporter);
            checkFieldNames(classNode, reporter);
            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.name,
                    "NamingConventionCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    /**
     * Checks that class name follows PascalCase convention.
     */
    private void checkClassName(ClassNode classNode, Reporter reporter) {
        // Extract simple class name (without package)
        String fullName = classNode.name;
        String simpleName = fullName.substring(fullName.lastIndexOf('/') + 1);

        // Skip anonymous classes (contain $)
        if (simpleName.contains("$")) {
            return;
        }

        if (!isPascalCase(simpleName)) {
            reporter.report(
                    classNode.name,
                    "Class name '" + simpleName + "' does not follow PascalCase convention"
            );
        }
    }

    /**
     * Checks that method names follow camelCase convention.
     */
    private void checkMethodNames(ClassNode classNode, Reporter reporter) {
        for (MethodNode method : classNode.methods) {
            // Skip constructors and static initializers
            if (method.name.equals("<init>") || method.name.equals("<clinit>")) {
                continue;
            }

            if (!isCamelCase(method.name)) {
                reporter.report(
                        classNode.name,
                        "Method name '" + method.name + "' does not follow camelCase convention"
                );
            }
        }
    }

    /**
     * Checks that field names follow camelCase convention,
     * except for constants which should be UPPER_SNAKE_CASE.
     */
    private void checkFieldNames(ClassNode classNode, Reporter reporter) {
        for (FieldNode field : classNode.fields) {
            boolean isConstant = isConstant(field);

            if (isConstant) {
                // Constants should be UPPER_SNAKE_CASE
                if (!isUpperSnakeCase(field.name)) {
                    reporter.report(
                            classNode.name,
                            "Constant field '" + field.name + "' does not follow UPPER_SNAKE_CASE convention"
                    );
                }
            } else {
                // Regular fields should be camelCase
                if (!isCamelCase(field.name)) {
                    reporter.report(
                            classNode.name,
                            "Field name '" + field.name + "' does not follow camelCase convention"
                    );
                }
            }
        }
    }

    /**
     * Checks if a name follows PascalCase (starts with uppercase letter).
     */
    private boolean isPascalCase(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // Must start with uppercase letter
        if (!Character.isUpperCase(name.charAt(0))) {
            return false;
        }

        // Should contain only letters and digits, no underscores (except inner classes)
        return name.matches("[A-Z][a-zA-Z0-9]*");
    }

    /**
     * Checks if a name follows camelCase (starts with lowercase letter).
     */
    private boolean isCamelCase(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // Must start with lowercase letter
        if (!Character.isLowerCase(name.charAt(0))) {
            return false;
        }

        // Should contain only letters and digits, no underscores
        return name.matches("[a-z][a-zA-Z0-9]*");
    }

    /**
     * Checks if a name follows UPPER_SNAKE_CASE.
     */
    private boolean isUpperSnakeCase(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // Should be all uppercase with underscores
        return name.matches("[A-Z][A-Z0-9_]*");
    }

    /**
     * Determines if a field is a constant (static final).
     */
    private boolean isConstant(FieldNode field) {
        return (field.access & Opcodes.ACC_STATIC) != 0 &&
               (field.access & Opcodes.ACC_FINAL) != 0;
    }
}
