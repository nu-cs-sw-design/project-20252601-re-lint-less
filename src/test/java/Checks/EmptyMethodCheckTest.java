package Checks;

import BytecodeParser.ASM.ASMParser;
import BytecodeParser.IClass;
import BytecodeParser.IClassParser;
import BytecodeParser.Parser;
import Reporting.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmptyMethodCheckTest {

    private Parser parser;
    private EmptyMethodCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new EmptyMethodCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectEmptyMethodsInGodClassExample() throws IOException {
        IClass clazz = parser.parse("examples.GodClassExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        // GodClassExample has 21 empty methods (method1 through method21)
        assertTrue(messages.size() >= 21);
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'method1' only contains a return statement")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'method10' only contains a return statement")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'method21' only contains a return statement")));
    }

    @Test
    void shouldDetectEmptyMethodsInTooManyParamsExample() throws IOException {
        IClass clazz = parser.parse("examples.TooManyParamsExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'fine' only contains a return statement")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'tooMany' only contains a return statement")));
    }

    private static class RecordingReporter implements Reporter {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void report(String className, String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
