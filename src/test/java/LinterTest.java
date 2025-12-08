import BytecodeParser.ASM.ASMParser;
import BytecodeParser.IClass;
import BytecodeParser.IClassParser;
import BytecodeParser.Parser;
import Checks.Check;
import Reporting.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinterTest {

    private Parser parser;
    private MockReporter mockReporter;

    @BeforeEach
    void setUp() {
        IClassParser asmParser = new ASMParser();
        parser = new Parser(asmParser);
        mockReporter = new MockReporter();
    }

    @Test
    void testRunMultiple_withMultipleClasses() {
        // Use classes that exist in the examples package
        List<String> classNames = List.of(
                "examples.PublicFieldExample",
                "examples.MagicNumbersExample"
        );

        // Create linter with a check that always reports something
        List<Check> checks = List.of(new ReportingCheck());
        Linter linter = new Linter(parser, checks);

        linter.runMultiple(classNames, mockReporter);

        // Verify both classes were processed
        assertEquals(2, mockReporter.getReports().size(),
                "Expected exactly 2 reports (one per class)");

        String r1 = mockReporter.getReports().get(0);
        String r2 = mockReporter.getReports().get(1);

        assertTrue(r1.contains("examples/PublicFieldExample") || r2.contains("examples/PublicFieldExample"),
                "Expected a report for examples/PublicFieldExample");
        assertTrue(r1.contains("examples/MagicNumbersExample") || r2.contains("examples/MagicNumbersExample"),
                "Expected a report for examples/MagicNumbersExample");
    }

    @Test
    void testRunMultiple_withEmptyList() {
        List<String> classNames = List.of();

        List<Check> checks = List.of(new MockCheck());
        Linter linter = new Linter(parser, checks);

        linter.runMultiple(classNames, mockReporter);

        // Should complete without error, no reports
        assertEquals(0, mockReporter.getReports().size());
    }

    @Test
    void testRunMultiple_withInvalidClass() {
        List<String> classNames = List.of("NonExistentClass");

        List<Check> checks = List.of(new MockCheck());
        Linter linter = new Linter(parser, checks);

        linter.runMultiple(classNames, mockReporter);

        // Linter should report parse failure for the invalid class
        assertEquals(1, mockReporter.getReports().size());
        String msg = mockReporter.getReports().get(0);
        assertTrue(msg.contains("NonExistentClass"), "Report should mention the invalid class name");
        assertTrue(msg.contains("Failed to parse"), "Report should indicate parse failure");
    }

    @Test
    void testLinter_withNoChecks() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        // Create linter with no checks
        Linter linter = new Linter(parser, List.of());

        linter.runMultiple(classNames, mockReporter);

        // Should parse successfully but produce no reports (no checks to run)
        assertEquals(0, mockReporter.getReports().size());
    }

    @Test
    void testLinter_withNullChecksList() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        // Create linter with null checks
        Linter linter = new Linter(parser, null);

        linter.runMultiple(classNames, mockReporter);

        // Should handle null gracefully and produce no reports
        assertEquals(0, mockReporter.getReports().size());
    }

    @Test
    void testLinter_checkReceivesCorrectClass() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        CapturingCheck capturingCheck = new CapturingCheck();
        Linter linter = new Linter(parser, List.of(capturingCheck));

        linter.runMultiple(classNames, mockReporter);

        // Verify the check received the correct class
        assertEquals(1, capturingCheck.classesChecked.size());
        assertEquals("examples/PublicFieldExample",
                capturingCheck.classesChecked.get(0).getClassName());
    }

    @Test
    void testLinter_withMultipleChecks() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        ReportingCheck check1 = new ReportingCheck();
        ReportingCheck check2 = new ReportingCheck();
        Linter linter = new Linter(parser, List.of(check1, check2));

        linter.runMultiple(classNames, mockReporter);

        // Should run both checks, producing 2 reports
        assertEquals(2, mockReporter.getReports().size());
    }

    @Test
    void testLinter_checkThrowsException() {
        List<String> classNames = List.of("examples.PublicFieldExample");

        ThrowingCheck throwingCheck = new ThrowingCheck();
        Linter linter = new Linter(parser, List.of(throwingCheck));

        linter.runMultiple(classNames, mockReporter);

        // Should catch exception and report it
        assertEquals(1, mockReporter.getReports().size());
        String msg = mockReporter.getReports().get(0);
        assertTrue(msg.contains("Check crashed"));
        assertTrue(msg.contains("ThrowingCheck"));
    }

    @Test
    void testRunMultiple_withNullClassNamesList() {
        Linter linter = new Linter(parser, List.of(new MockCheck()));

        // Should handle null gracefully
        assertDoesNotThrow(() -> linter.runMultiple(null, mockReporter));
        assertEquals(0, mockReporter.getReports().size());
    }

    // Mock classes for testing

    private static class MockReporter implements Reporter {
        private final List<String> reports = new ArrayList<>();

        @Override
        public void report(String className, String message) {
            reports.add(className + ": " + message);
        }

        public List<String> getReports() {
            return reports;
        }
    }

    private static class MockCheck implements Check {
        @Override
        public boolean apply(IClass clazz, Reporter reporter) {
            return true;
        }
    }

    private static class ReportingCheck implements Check {
        @Override
        public boolean apply(IClass clazz, Reporter reporter) {
            reporter.report(clazz.getClassName(), "Check executed");
            return true;
        }
    }

    private static class CapturingCheck implements Check {
        public final List<IClass> classesChecked = new ArrayList<>();

        @Override
        public boolean apply(IClass clazz, Reporter reporter) {
            classesChecked.add(clazz);
            return true;
        }
    }

    private static class ThrowingCheck implements Check {
        @Override
        public boolean apply(IClass clazz, Reporter reporter) {
            throw new RuntimeException("Test exception");
        }
    }
}
