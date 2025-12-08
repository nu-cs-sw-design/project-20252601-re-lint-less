package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IField;
import BytecodeParser.IMethod;
import Reporting.Reporter;

public class NamingConventionCheck implements Check {

    @Override
    public boolean apply(IClass classNode, Reporter reporter) {
        try {
            checkClassName(classNode, reporter);
            checkMethodNames(classNode, reporter);
            checkFieldNames(classNode, reporter);
            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.getClassName(),
                    "NamingConventionCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    private void checkClassName(IClass classNode, Reporter reporter) {
        String fullName = classNode.getClassName();
        String simpleName = fullName.substring(fullName.lastIndexOf('/') + 1);

        // Skip anonymous classes (contain $)
        if (simpleName.contains("$")) {
            return;
        }

        if (!isPascalCase(simpleName)) {
            reporter.report(
                    classNode.getClassName(),
                    "Class name '" + simpleName + "' does not follow PascalCase convention"
            );
        }
    }

    private void checkMethodNames(IClass classNode, Reporter reporter) {
        for (IMethod method : classNode.getMethods()) {
            String name = method.getName();
            // Skip constructors
            if (name.equals("<init>") || name.equals("<clinit>")) continue;

            if (!isCamelCase(name)) {
                reporter.report(
                        classNode.getClassName(),
                        "Method name '" + name + "' does not follow camelCase convention"
                );
            }
        }
    }

    private void checkFieldNames(IClass classNode, Reporter reporter) {
        for (IField field : classNode.getFields()) {
            boolean isConstant = field.isStatic() && field.isFinal();
            String name = field.getName();

            if (isConstant) {
                if (!isUpperSnakeCase(name)) {
                    reporter.report(
                            classNode.getClassName(),
                            "Constant field '" + name + "' does not follow UPPER_SNAKE_CASE convention"
                    );
                }
            } else {
                if (!isCamelCase(name)) {
                    reporter.report(
                            classNode.getClassName(),
                            "Field name '" + name + "' does not follow camelCase convention"
                    );
                }
            }
        }
    }

    private boolean isPascalCase(String name) {
        if (name == null || name.isEmpty()) return false;
        if (!Character.isUpperCase(name.charAt(0))) return false;
        return name.matches("[A-Z][a-zA-Z0-9]*");
    }

    private boolean isCamelCase(String name) {
        if (name == null || name.isEmpty()) return false;
        if (!Character.isLowerCase(name.charAt(0))) return false;
        return name.matches("[a-z][a-zA-Z0-9]*");
    }

    private boolean isUpperSnakeCase(String name) {
        if (name == null || name.isEmpty()) return false;
        return name.matches("[A-Z][A-Z0-9_]*");
    }
}
