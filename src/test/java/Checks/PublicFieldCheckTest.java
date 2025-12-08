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

class PublicFieldCheckTest {

    private Parser parser;
    private PublicFieldCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new PublicFieldCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectPublicFields() throws IOException {
        IClass clazz = parser.parse("examples.PublicFieldExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.size() > 0, "Expected at least one public field violation but got none");
        // Check for either "badField" or general public field message
        assertTrue(messages.stream().anyMatch(m ->
            m.toLowerCase().contains("field") && (m.contains("public") || m.contains("badField"))
        ), "Expected message about public field but got: " + messages);
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
