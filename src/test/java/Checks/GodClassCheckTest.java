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

class GodClassCheckTest {

    private Parser parser;
    private GodClassCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new GodClassCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectGodClassWithTooManyMethods() throws IOException {
        IClass clazz = parser.parse("examples.GodClassExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Class has 22 methods (max allowed: 20) - possible God Class")));
    }

    @Test
    void shouldDetectGodClassWithTooManyFields() throws IOException {
        IClass clazz = parser.parse("examples.GodClassExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Class has 11 fields (max allowed: 10) - possible God Class")));
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
