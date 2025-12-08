package BytecodeParser;

public interface IInstruction {
	int getOpcode();
    ILabel getJumpLabel();
	boolean isPushConstant();
	Number getConstantValue();
    Integer getVarIndex();
}
