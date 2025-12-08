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

class UnusedVariablesCheckTest {

    private Parser parser;
    private UnusedVariablesCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new UnusedVariablesCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectUnusedVariablesInUnusedVariablesExample() throws IOException {
        IClass clazz = parser.parse("examples.UnusedVariablesExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertEquals(5, messages.size());
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'hasUnusedVariable' declares unused variable 'unused'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'multipleUnused' declares unused variable 'temp'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'multipleUnused' declares unused variable 'result'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'assignedButNeverRead' declares unused variable 'computed'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'complexUnused' declares unused variable 'doubled'")));
    }

    @Test
    void shouldDetectUnusedVariablesInComprehensiveExample() throws IOException {
        IClass clazz = parser.parse("examples.ComprehensiveExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'processOrder' declares unused variable 'tempValue'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'processOrder' declares unused variable 'tax'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'calculateTotal' declares unused variable 'subtotal'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'inefficientCalculation' declares unused variable 'c'")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method 'inefficientCalculation' declares unused variable 'product'")));
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
