package BytecodeParser;

public interface IField {
    String getName();
    String getType();
    boolean isStatic();
    boolean isFinal();
    boolean isPublic();
}
