package BytecodeParser;

import java.io.IOException;

public class Parser {
    private final IClassParser parser;

    public Parser(IClassParser parser) {
        this.parser = parser;
    }

    public IClass parse(String className) throws IOException {
        return parser.parse(className);
    }
}