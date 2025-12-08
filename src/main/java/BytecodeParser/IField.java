package BytecodeParser;

public interface IField {
    String getName();
    boolean isStatic();
    boolean isFinal();
    boolean isPublic();
}
