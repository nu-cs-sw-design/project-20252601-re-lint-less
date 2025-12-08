package BytecodeParser;

public interface IInstruction {
	int getOpcode();      // like ASM Opcodes
    boolean isJump();     // true if this instruction is a jump
    ILabel getJumpLabel(); // returns the target label if it’s a jump, null otherwise
	boolean isPushConstant();
	Number getConstantValue();
    Integer getVarIndex();
}
