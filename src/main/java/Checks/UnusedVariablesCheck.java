package Checks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import Reporting.Reporter;

import java.util.HashSet;
import java.util.Set;

/**
 * Detects local variables that are declared but never used.
 * This check analyzes local variable tables and tracks which variables
 * are loaded during method execution. Variables that are never loaded
 * are considered unused and reported.
 */
public class UnusedVariablesCheck implements Check {

    @Override
    public boolean apply(ClassNode classNode, Reporter reporter) {
        try {
            for (MethodNode method : classNode.methods) {
                checkForUnusedVariables(classNode, method, reporter);
            }
            return true;
        } catch (Exception e) {
            reporter.report(classNode.name, "UnusedVariablesCheck failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks a single method for unused local variables.
     */
    private void checkForUnusedVariables(ClassNode classNode, MethodNode method, Reporter reporter) {
        // Skip methods without local variable information
        if (method.localVariables == null || method.localVariables.isEmpty()) {
            return;
        }

        // Track which variable indices are loaded
        Set<Integer> loadedVariables = new HashSet<>();

        // Scan instructions to find all variable loads
        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof VarInsnNode) {
                VarInsnNode varInsn = (VarInsnNode) insn;

                // Check if this is a load instruction
                if (isLoadInstruction(varInsn.getOpcode())) {
                    loadedVariables.add(varInsn.var);
                }
            }
        }

        // Check each local variable to see if it was ever loaded
        for (LocalVariableNode localVar : method.localVariables) {
            // Skip "this" parameter for instance methods
            if (localVar.index == 0 && !isStatic(method)) {
                continue;
            }

            // Skip method parameters (they're passed in, so being unused might be intentional
            // for interface compliance or overriding). We only check truly local variables.
            if (isMethodParameter(method, localVar)) {
                continue;
            }

            // Check if this variable was ever loaded
            if (!loadedVariables.contains(localVar.index)) {
                reporter.report(
                    classNode.name,
                    "Method '" + method.name + "' declares unused variable '" + localVar.name + "'"
                );
            }
        }
    }

    /**
     * Checks if an opcode is a variable load instruction.
     */
    private boolean isLoadInstruction(int opcode) {
        return opcode == Opcodes.ILOAD ||  // int load
               opcode == Opcodes.LLOAD ||  // long load
               opcode == Opcodes.FLOAD ||  // float load
               opcode == Opcodes.DLOAD ||  // double load
               opcode == Opcodes.ALOAD;    // reference load
    }

    /**
     * Checks if a method is static.
     */
    private boolean isStatic(MethodNode method) {
        return (method.access & Opcodes.ACC_STATIC) != 0;
    }

    /**
     * Determines if a local variable is a method parameter.
     * Parameters occupy the first N local variable slots, where N is the parameter count.
     * For instance methods, slot 0 is "this", then parameters start at slot 1.
     * For static methods, parameters start at slot 0.
     */
    private boolean isMethodParameter(MethodNode method, LocalVariableNode localVar) {
        // Count the number of parameter slots
        int paramSlots = 0;

        // For instance methods, slot 0 is "this"
        if (!isStatic(method)) {
            paramSlots = 1;
        }

        // Count parameter slots from method descriptor
        String descriptor = method.desc;
        int startIndex = descriptor.indexOf('(');
        int endIndex = descriptor.indexOf(')');

        if (startIndex >= 0 && endIndex > startIndex) {
            String params = descriptor.substring(startIndex + 1, endIndex);
            paramSlots += countParameterSlots(params);
        }

        // If the variable index is less than the total parameter slots, it's a parameter
        return localVar.index < paramSlots;
    }

    /**
     * Counts the number of local variable slots used by parameters.
     * Note: long and double types take 2 slots, all others take 1 slot.
     */
    private int countParameterSlots(String paramDescriptor) {
        int slots = 0;
        int i = 0;

        while (i < paramDescriptor.length()) {
            char c = paramDescriptor.charAt(i);

            switch (c) {
                case 'B': // byte
                case 'C': // char
                case 'F': // float
                case 'I': // int
                case 'S': // short
                case 'Z': // boolean
                    slots++;
                    i++;
                    break;

                case 'D': // double (takes 2 slots)
                case 'J': // long (takes 2 slots)
                    slots += 2;
                    i++;
                    break;

                case 'L': // object reference
                    slots++;
                    // Skip until we find the semicolon
                    while (i < paramDescriptor.length() && paramDescriptor.charAt(i) != ';') {
                        i++;
                    }
                    i++; // skip the semicolon
                    break;

                case '[': // array
                    // Arrays take 1 slot, but we need to skip the array descriptor
                    slots++;
                    i++;
                    // Skip array dimensions
                    while (i < paramDescriptor.length() && paramDescriptor.charAt(i) == '[') {
                        i++;
                    }
                    // Skip the element type
                    if (i < paramDescriptor.length()) {
                        char elementType = paramDescriptor.charAt(i);
                        if (elementType == 'L') {
                            // Object type, skip until semicolon
                            while (i < paramDescriptor.length() && paramDescriptor.charAt(i) != ';') {
                                i++;
                            }
                            i++; // skip semicolon
                        } else {
                            // Primitive type
                            i++;
                        }
                    }
                    break;

                default:
                    i++; // Skip unknown characters
                    break;
            }
        }

        return slots;
    }
}