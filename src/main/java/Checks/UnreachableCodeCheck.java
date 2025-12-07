package Checks;

import BytecodeParser.IClass;
import BytecodeParser.IMethod;
import Reporting.Reporter;

import java.util.List;

public class UnreachableCodeCheck implements Check {

    @Override
    public boolean apply(IClass classNode, Reporter reporter) {
        try {
            for (IMethod method : classNode.getMethods()) {
                List<Integer> opcodes = method.getOpcodes();
                if (opcodes == null || opcodes.isEmpty()) continue;

                boolean unreachable = false;

                for (int opcode : opcodes) {

                    if (unreachable) {
                        reporter.report(
                                classNode.getClassName(),
                                "Unreachable code detected in method '" + method.getName() + "'"
                        );
                        unreachable = false; // only report once per block
                    }

                    if (isTerminal(opcode)) {
                        unreachable = true;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            reporter.report(
                    classNode.getClassName(),
                    "UnreachableCodeCheck failed: " + e.getMessage()
            );
            return false;
        }
    }

    private boolean isTerminal(int opcode) {
        switch (opcode) {
            case -1: return false;
            case org.objectweb.asm.Opcodes.RETURN:
            case org.objectweb.asm.Opcodes.IRETURN:
            case org.objectweb.asm.Opcodes.LRETURN:
            case org.objectweb.asm.Opcodes.FRETURN:
            case org.objectweb.asm.Opcodes.DRETURN:
            case org.objectweb.asm.Opcodes.ARETURN:
            case org.objectweb.asm.Opcodes.ATHROW:
                return true;
            default:
                return false;
        }
    }
}