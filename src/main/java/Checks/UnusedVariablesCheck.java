package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import BytecodeParser.IInstruction;
import BytecodeParser.ILocalVariable;
import Reporting.Reporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects local variables that are declared but never used.
 * Works with the BytecodeParser wrapper interfaces.
 */

/*
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

        // Scan instructions to find variable loads
        for (IInstruction insn : method.getInstructions()) {
            if (isLoadInstruction(insn.getOpcode())) {
                if (insn instanceof BytecodeParser.ASM.ASMParser.ASMClass.ASMInstruction) {
                    BytecodeParser.ASM.ASMParser.ASMClass.ASMInstruction asmInsn =
                            (BytecodeParser.ASM.ASMParser.ASMClass.ASMInstruction) insn;
                    Integer varIndex = asmInsn.getVarIndex(); // you may need to expose var index in ASMInstruction
                    if (varIndex != null) {
                        loadedVariables.add(varIndex);
                    }
                }
            }
        }

        for (ILocalVariable localVar : locals) {
            // Skip "this" for instance methods
            if (localVar.getIndex() == 0 && !method.isStatic()) continue;

            // Skip method parameters
            if (isMethodParameter(method, localVar)) continue;

            if (!loadedVariables.contains(localVar.getIndex())) {
                reporter.report(className,
                        "Method '" + method.getName() + "' declares unused variable '" + localVar.getName() + "'");
            }
        }
    }

    private boolean isLoadInstruction(int opcode) {
        return (opcode >= 21 && opcode <= 25); // ILOAD, LLOAD, FLOAD, DLOAD, ALOAD
    }

    private boolean isMethodParameter(IMethod method, ILocalVariable localVar) {
        int paramSlots = method.isStatic() ? 0 : 1; // account for "this"
        paramSlots += method.getParameterCount(); // total parameter slots
        return localVar.getIndex() < paramSlots;
    }
}

*/
