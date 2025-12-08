package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import BytecodeParser.IInstruction;
import BytecodeParser.ILocalVariable;
import Reporting.Reporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnusedVariablesCheck implements Check {

    @Override
    public boolean apply(IClass clazz, Reporter reporter) {
        try {
            for (IMethod method : clazz.getMethods()) {
                checkForUnusedVariables(clazz.getClassName(), method, reporter);
            }
            return true;
        } catch (Exception e) {
            reporter.report(clazz.getClassName(),
                    "UnusedVariablesCheck failed: " + e.getMessage());
            return false;
        }
    }

    private void checkForUnusedVariables(String className, IMethod method, Reporter reporter) {
        List<ILocalVariable> locals = method.getLocalVariables();
        if (locals == null || locals.isEmpty()) return;

        Set<Integer> loadedVariables = new HashSet<>();

        for (IInstruction insn : method.getInstructions()) {
            if (isLoadInstruction(insn.getOpcode())) {
                Integer varIndex = insn.getVarIndex();
                if (varIndex != null) {
                    loadedVariables.add(varIndex);
                }
            }
        }

        for (ILocalVariable localVar : locals) {
            if (localVar.getIndex() == 0 && !method.isStatic()) {
                continue;
            }

            if (isMethodParameter(method, localVar)) {
                continue;
            }

            if (!loadedVariables.contains(localVar.getIndex())) {
                reporter.report(
                        className,
                        "Method '" + method.getName() +
                                "' declares unused variable '" + localVar.getName() + "'"
                );
            }
        }
    }

    private boolean isLoadInstruction(int opcode) {
        // ILOAD, LLOAD, FLOAD, DLOAD, ALOAD
        return opcode >= 21 && opcode <= 25;
    }

    private boolean isMethodParameter(IMethod method, ILocalVariable localVar) {
        int paramSlots = method.isStatic() ? 0 : 1;

        String descriptor = method.getDescriptor();
        int startIndex = descriptor.indexOf('(');
        int endIndex = descriptor.indexOf(')');

        if (startIndex >= 0 && endIndex > startIndex) {
            String params = descriptor.substring(startIndex + 1, endIndex);
            paramSlots += countParameterSlots(params);
        }

        return localVar.getIndex() < paramSlots;
    }

    private int countParameterSlots(String paramDescriptor) {
        int slots = 0;
        int i = 0;

        while (i < paramDescriptor.length()) {
            char c = paramDescriptor.charAt(i);

            switch (c) {
                case 'B': case 'C': case 'F':
                case 'I': case 'S': case 'Z':
                    slots++;
                    i++;
                    break;

                case 'D': // double
                case 'J': // long
                    slots += 2;
                    i++;
                    break;

                case 'L': // Object type
                    slots++;
                    while (i < paramDescriptor.length() && paramDescriptor.charAt(i) != ';') {
                        i++;
                    }
                    i++; // skip ';'
                    break;

                case '[': // Array type
                    slots++;
                    i++;
                    while (i < paramDescriptor.length() && paramDescriptor.charAt(i) == '[') {
                        i++;
                    }
                    if (i < paramDescriptor.length()) {
                        if (paramDescriptor.charAt(i) == 'L') {
                            while (i < paramDescriptor.length() && paramDescriptor.charAt(i) != ';') {
                                i++;
                            }
                            i++; // skip ';'
                        } else {
                            i++; // primitive element
                        }
                    }
                    break;

                default:
                    i++; // skip unknown / unexpected
                    break;
            }
        }

        return slots;
    }
}
