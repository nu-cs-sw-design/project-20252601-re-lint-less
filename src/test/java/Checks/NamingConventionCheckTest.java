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

class NamingConventionCheckTest {

    private Parser parser;
    private NamingConventionCheck check;
    private RecordingReporter reporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        check = new NamingConventionCheck();
        reporter = new RecordingReporter();
    }

    @Test
    void shouldDetectBadClassName() throws IOException {
        IClass clazz = parser.parse("examples.badClassName");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Class name 'badClassName' does not follow PascalCase convention")));
    }

    @Test
    void shouldDetectBadMethodNames() throws IOException {
        IClass clazz = parser.parse("examples.NamingConventionExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method name 'CalculateSum' does not follow camelCase convention")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method name 'get_user_name' does not follow camelCase convention")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Method name 'PROCESS' does not follow camelCase convention")));
    }

    @Test
    void shouldDetectBadFieldNames() throws IOException {
        IClass clazz = parser.parse("examples.NamingConventionExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Field name 'UserName' does not follow camelCase convention")));
        assertTrue(messages.stream().anyMatch(m -> m.contains("Field name 'user_age' does not follow camelCase convention")));
    }

    @Test
    void shouldDetectBadConstantName() throws IOException {
        IClass clazz = parser.parse("examples.NamingConventionExample");
        check.apply(clazz, reporter);

        List<String> messages = reporter.getMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Constant field 'apiKey' does not follow UPPER_SNAKE_CASE convention")));
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