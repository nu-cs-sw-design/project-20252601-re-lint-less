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

class RedundantInterfacesCheckTest {

    private Parser parser;
    private RedundantInterfacesCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new RedundantInterfacesCheck(asmParser);
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectRedundantInterface() throws IOException {
        IClass clazz = parser.parse("examples.RedundantExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m ->
            m.contains("Interface 'examples/A' is redundantly declared") &&
            m.contains("already inherited from the superclass or another implemented interface")
        ));
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