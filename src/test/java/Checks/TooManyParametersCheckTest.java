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

class TooManyParametersCheckTest {

    private Parser parser;
    private TooManyParametersCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new TooManyParametersCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectTooManyParametersInTooManyParamsExample() throws IOException {
        IClass clazz = parser.parse("examples.TooManyParamsExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'tooMany' has 6 parameters (max allowed: 5)")));
    }

    @Test
    void shouldDetectTooManyParametersInTooManyParametersExample() throws IOException {
        IClass clazz = parser.parse("examples.TooManyParametersExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'tooManyParams' has 6 parameters (max allowed: 5)")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'calculateComplexValue' has 7 parameters (max allowed: 5)")));
    }

    @Test
    void shouldDetectTooManyParametersInComprehensiveExample() throws IOException {
        IClass clazz = parser.parse("examples.ComprehensiveExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'processOrder' has 8 parameters (max allowed: 5)")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'calculateTotal' has 6 parameters (max allowed: 5)")));
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
