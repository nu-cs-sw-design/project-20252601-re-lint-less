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

class MagicNumberCheckTest {

    private Parser parser;
    private MagicNumberCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new MagicNumberCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectMagicNumbersInMagicNumbersExample() throws IOException {
        IClass clazz = parser.parse("examples.MagicNumbersExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("Magic number 123 found in method <init>"));
    }

    @Test
    void shouldDetectMagicNumbersInUnusedVariablesExample() throws IOException {
        IClass clazz = parser.parse("examples.UnusedVariablesExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.size() >= 10); // Multiple magic numbers
        assertTrue(messages.stream().anyMatch(m -> m.contains("Magic number 10 found in method hasUnusedVariable")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Magic number 20 found in method hasUnusedVariable")));
    }

    @Test
    void shouldDetectMagicNumbersInComprehensiveExample() throws IOException {
        IClass clazz = parser.parse("examples.ComprehensiveExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Magic number 10 found in method processOrder")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Magic number 1.15 found in method processOrder")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Magic number 30 found in method inefficientCalculation")));
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
