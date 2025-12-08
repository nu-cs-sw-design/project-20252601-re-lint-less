package BytecodeParser;

import java.util.List;

public interface IMethod {
	String getName();
    int getParameterCount();
    List<String> getInstructionTypes(); // optional helper
	List<Integer> getOpcodes();
	List<IInstruction> getInstructions();
	List<ILocalVariable> getLocalVariables();
    String getDescriptor();
    boolean isStatic();
}
