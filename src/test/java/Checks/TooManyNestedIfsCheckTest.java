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

class TooManyNestedIfsCheckTest {

    private Parser parser;
    private TooManyNestedIfsCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new TooManyNestedIfsCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectDeeplyNestedIfs() throws IOException {
        IClass clazz = parser.parse("examples.NestedIfExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.size() > 0);
        assertTrue(messages.stream().anyMatch(m ->
            m.contains("has") && m.contains("nested") && m.contains("depth")
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
