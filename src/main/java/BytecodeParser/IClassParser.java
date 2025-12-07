package BytecodeParser;

import java.io.IOException;

public interface IClassParser {
    IClass parse(String className) throws IOException;
}
